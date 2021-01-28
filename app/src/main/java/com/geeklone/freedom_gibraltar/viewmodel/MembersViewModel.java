package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.views.activities.ConversationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MembersViewModel extends AndroidViewModel {

    private MutableLiveData<List<User>> liveData;
    List<User> arrayList = new ArrayList<>();
    Application application;
    SessionManager sessionManager;

    public MembersViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }


    public MutableLiveData<List<User>> getMembers() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<User>>();
            sessionManager = new SessionManager(application.getApplicationContext());
            fetchRecord();
        }
        return liveData;
    }

    private void fetchRecord() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!sessionManager.getUid().equals(snapshot.child("id").getValue(String.class))) {
                                    User user = snapshot.getValue(User.class);
                                    arrayList.add(user);
                                }
                            }

                            liveData.setValue(arrayList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });
    }

    public void startNewChat(View view, User user) {
        view.getContext().startActivity(new Intent(view.getContext(), ConversationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("user", user)
        );
        ((Activity)view.getContext()).finish();
    }

}

