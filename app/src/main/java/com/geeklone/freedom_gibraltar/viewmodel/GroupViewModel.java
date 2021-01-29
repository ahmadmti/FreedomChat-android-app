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
import com.geeklone.freedom_gibraltar.views.activities.GroupConversationActivity;
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
        FirebaseDatabase.getInstance().getReference("groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            Log.e("TAG", "onDataChange: " + dataSnapshot);
                            for (DataSnapshot snapshotMain : dataSnapshot.getChildren()) {
                                DataSnapshot snapshot = snapshotMain.child("groupInfo");
                                for (DataSnapshot snapshot1 : snapshot.child("members").getChildren()) {
                                    if (snapshot1.getValue(String.class).equals(sessionManager.getUid())) {
                                        Group group = snapshot.getValue(Group.class);
                                        arrayList.add(group);
                                    }
                                }
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

    public void startChat(View view, Group group) {
        view.getContext().startActivity(new Intent(view.getContext(), GroupConversationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("group", group)
        );
    }

}

