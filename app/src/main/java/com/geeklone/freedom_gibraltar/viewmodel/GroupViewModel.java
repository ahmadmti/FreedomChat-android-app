package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.views.activities.ConversationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    private MutableLiveData<List<Group>> liveData;
    List<Group> arrayList = new ArrayList<>();
    Application application;
    SessionManager sessionManager;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public MutableLiveData<List<Group>> getGroups() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<Group>>();
            sessionManager = new SessionManager(application.getApplicationContext());
            fetchRecord();
        }
        return liveData;
    }

    private void fetchRecord() {
        FirebaseDatabase.getInstance().getReference("groups").child(sessionManager.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            Log.e("TAG", "onDataChange: " + dataSnapshot);
                            for (DataSnapshot snapshotMain : dataSnapshot.getChildren()) {
                                DataSnapshot snapshot = snapshotMain.child("userInfo");
                                Log.e("TAG", "snapshot: " + snapshot);
                                Group user = snapshot.getValue(Group.class);
                                arrayList.add(user);
                            }
                        }

                        liveData.setValue(arrayList);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });
    }

    public void startChat(View view, Group chat) {
        view.getContext().startActivity(new Intent(view.getContext(), ConversationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("chat", chat)
        );
    }

}

