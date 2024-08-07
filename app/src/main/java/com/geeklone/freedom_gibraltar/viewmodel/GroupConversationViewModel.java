package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.Group;
import com.github.dhaval2404.imagepicker.ImagePicker;
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

public class GroupConversationViewModel extends AndroidViewModel {

    public MutableLiveData<String> msg = new MutableLiveData<>();

    private MutableLiveData<DataSnapshot> liveData;
    List<Conversation> arrayList = new ArrayList<>();
    Application application;
    SessionManager sessionManager;
    DatabaseReference reference;
    Group group;
    private String user_msg_key;
    FirebasePush firebasePush;
    BroadcastReceiver broadcastReceiver;


    public GroupConversationViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void init(Group group) {
        this.group = group;
        reference=FirebaseDatabase.getInstance().getReference().child("groups").child(group.getId());
        sessionManager = new SessionManager(application.getApplicationContext());
        initpushNoti();
        fetchMessages();
        setupBroadCast();
    }
    private void setupBroadCast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                String resultUri = i.getStringExtra("URI");
                String   name =i.getStringExtra("name");
                sendImage(resultUri, name);
                
                
            }
        };

        application.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("getImgURI"));
    }

    public MutableLiveData<DataSnapshot> getConversation() {
        if (liveData == null) {
            liveData = new MutableLiveData<DataSnapshot>();
        }
        return liveData;
    }

    private void fetchMessages() {
        reference.child("conversation")
                .addChildEventListener(
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

    public void attachImg(View view) {
        ImagePicker.Companion.with((Activity)view.getContext())
                .compress(512)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    public void sendImage(String uri, String name) {
        Log.i(TAG, "sendImage: ");
        DatabaseReference  referenceInfo = reference.child("groupInfo");
        DatabaseReference databaseReference =reference.child("conversation");

        //payload
        Map<String, Object> map = new HashMap<String, Object>();
        user_msg_key = databaseReference.push().getKey();

        databaseReference.updateChildren(map);

        DatabaseReference dbrSender = databaseReference.child(user_msg_key);
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", user_msg_key);
        hashMap.put("msg", uri);
        hashMap.put("timeStamp", String.valueOf(Utils.getSysTimeStamp()));
        hashMap.put("msgType", "img");
        hashMap.put("imgName", name);
        hashMap.put("name", sessionManager.getUserName());
        hashMap.put("from", sessionManager.getUid());

        Log.e(TAG, "sendMessage: ");
        dbrSender.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            iv_sendMsg.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
                            referenceInfo.child("lastMsg").setValue(name);
                            referenceInfo.child("lastMsgBy").setValue(sessionManager.getUserName());
                            referenceInfo.child("updatedDate").setValue(String.valueOf(Utils.getSysTimeStamp()));

                            msg.setValue("");
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

    }


    public void sendMessage(View view) {
        if (msg.getValue() == null || msg.getValue().isEmpty())
            return;

//        iv_sendMsg.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference  referenceInfo = reference.child("groupInfo");
//        Map<String, Object> userInfo = new HashMap<String, Object>();
//        userInfo.put("chatUserId", group.getId());
//        userInfo.put("chatUserName", group.getName());
//        userInfo.put("chatUserImg", sessionManager.getUserImg());
//        userInfo.put("deviceToken", sessionManager.getDeviceToken());
//        referenceInfo.updateChildren(userInfo);
//
//        DatabaseReference  referenceInfoReceiver = reference.child(group.getId()).child(sessionManager.getUid()).child("userInfo");
//        userInfo = new HashMap<String, Object>();
//        userInfo.put("chatUserId", sessionManager.getUid());
//        userInfo.put("chatUserName", sessionManager.getUserName());
//        userInfo.put("chatUserImg", group.getProfileImg());
//        userInfo.put("deviceToken", group.getDeviceToken());
//        referenceInfoReceiver.updateChildren(userInfo);


        DatabaseReference databaseReference =reference.child("conversation");
//        DatabaseReference referenceReceiver = reference.child(group.getId()).child(sessionManager.getUid()).child("conversation");

        //payload
        Map<String, Object> map = new HashMap<String, Object>();
        user_msg_key = databaseReference.push().getKey();

        databaseReference.updateChildren(map);
//        referenceReceiver.updateChildren(map);


        DatabaseReference dbrSender = databaseReference.child(user_msg_key);
//        DatabaseReference dbrReceiver = referenceReceiver.child(user_msg_key);
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", user_msg_key);
        hashMap.put("msg", msg.getValue().trim());
        hashMap.put("timeStamp", String.valueOf(Utils.getSysTimeStamp()));
        hashMap.put("msgType", "txt");
        hashMap.put("name", sessionManager.getUserName());
        hashMap.put("from", sessionManager.getUid());

        Log.e(TAG, "sendMessage: ");
        dbrSender.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            iv_sendMsg.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
                            referenceInfo.child("lastMsg").setValue(msg.getValue().trim());
                            referenceInfo.child("lastMsgBy").setValue(sessionManager.getUserName());
                            referenceInfo.child("updatedDate").setValue(String.valueOf(Utils.getSysTimeStamp()));

//                            referenceInfoReceiver.child("lastMsg").setValue(msg.getValue().trim());
//                            referenceInfoReceiver.child("updatedDate").setValue(String.valueOf(Utils.getSysTimeStamp()));
//                            referenceInfoReceiver.child("msgRead").setValue(false);

                            msg.setValue("");

                            //send Notification
//                            if (group.getDeviceToken() != null) {
//                                firebasePush.sendToToken(group.getDeviceToken()); // send to token
//                                firebasePush.setNotification(new Notification(group.getName(), msg.getValue().trim()));
//                            }

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

//        dbrReceiver.updateChildren(hashMap);

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

    @Override
    protected void onCleared() {
        super.onCleared();
        application.getApplicationContext().unregisterReceiver(broadcastReceiver);
    }
}

