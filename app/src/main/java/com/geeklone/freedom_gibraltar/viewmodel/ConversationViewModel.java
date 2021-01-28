package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcm.androidtoandroid.FirebasePush;
import fcm.androidtoandroid.connection.PushNotificationTask;
import fcm.androidtoandroid.model.Notification;

import static android.content.ContentValues.TAG;

public class ConversationViewModel extends AndroidViewModel {

    public MutableLiveData<String> msg = new MutableLiveData<>();

    private MutableLiveData<DataSnapshot> liveData;
    List<Conversation> arrayList = new ArrayList<>();
    Application application;
    SessionManager sessionManager;
    DatabaseReference databaseReference, databaseReferenceInfo;
    User user;
    private String user_msg_key;
    FirebasePush firebasePush;


    public ConversationViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void init(User user) {
        this.user = user;
        initpushNoti();
    }

    public MutableLiveData<DataSnapshot> getConversation() {
        if (liveData == null) {
            liveData = new MutableLiveData<DataSnapshot>();
            sessionManager = new SessionManager(application.getApplicationContext());
            fetchMessages();
        }
        return liveData;
    }

    private void fetchMessages() {
        databaseReferenceInfo = FirebaseDatabase.getInstance().getReference().child("chats").child(sessionManager.getUid()).child(user.getId()).child("userInfo");
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("chatUserId", user.getId());
        userInfo.put("chatUserName", user.getName());
        userInfo.put("chatUserImg", sessionManager.getUserImg());
        userInfo.put("deviceToken", sessionManager.getDeviceToken());
        databaseReferenceInfo.updateChildren(userInfo);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child(sessionManager.getUid()).child(user.getId()).child("conversation");
        databaseReference.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            liveData.setValue(dataSnapshot);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        liveData.setValue(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "onCancelled: " + String.valueOf(databaseError.getMessage()));

                    }
                }
        );
    }

    public void sendMessage(View view) {
        if (msg.getValue() == null || msg.getValue().isEmpty())
            return;

//        iv_sendMsg.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference().child("chats").child(sessionManager.getUid()).child(user.getId()).child("conversation");
        DatabaseReference referenceReceiver = FirebaseDatabase.getInstance().getReference().child("chats").child(user.getId()).child(sessionManager.getUid()).child("conversation");


        //payload
        Map<String, Object> map = new HashMap<String, Object>();
        user_msg_key = referenceSender.push().getKey();

        referenceSender.updateChildren(map);
        referenceReceiver.updateChildren(map);


        DatabaseReference dbrSender = referenceSender.child(user_msg_key);
        DatabaseReference dbrReceiver = referenceReceiver.child(user_msg_key);
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", user_msg_key);
        hashMap.put("msg", msg.getValue().trim());
        hashMap.put("timeStamp", String.valueOf(Utils.getSysTimeStamp()));
        hashMap.put("msgType", "text");
        hashMap.put("name", sessionManager.getUserName());
        hashMap.put("from", sessionManager.getUserID());

        Log.e(TAG, "sendMessage: ");
        dbrSender.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            iv_sendMsg.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
                            databaseReferenceInfo.child("lastMsg").setValue( msg.getValue().trim());
                            databaseReferenceInfo.child("updatedDate").setValue(String.valueOf(Utils.getSysTimeStamp()));
                            databaseReferenceInfo.child("msgRead").setValue(false);

                            msg.setValue("");

                            //send Notification
                            if (user.getDeviceToken() != null) {
                                firebasePush.sendToToken(user.getDeviceToken()); // send to token
                                firebasePush.setNotification(new Notification(user.getName(), msg.getValue().trim()));
                            }

                        } else
                            Utils.showToast(application.getApplicationContext(), "Message sending failed");

                        Log.e(TAG, "sendMessage: " + task.getResult());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.showToast(application.getApplicationContext(), "Message sending failed");
//                        iv_sendMsg.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "sendMessage: " + e.getMessage());

                    }
                });

        dbrReceiver.updateChildren(hashMap);

    }

    private void initpushNoti() {
        firebasePush = new FirebasePush(Utils.FIREBASE_SERVER_KEY);
        firebasePush.setAsyncResponse(new PushNotificationTask.AsyncResponse() {
            @Override
            public void onFinishPush(@NotNull String ouput) {
                Log.e("OUTPUT", ouput);
            }
        });
    }

}

