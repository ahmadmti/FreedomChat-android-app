package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private MutableLiveData<List<Chat>> liveData;
    List<Chat> arrayList= new ArrayList<>();
    Application application;
    SessionManager sessionManager ;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
    }

    public MutableLiveData<List<Chat>> getChats() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<Chat>>();
            sessionManager = new SessionManager(application.getApplicationContext());
        fetchRecord();
        }
        return liveData;
    }

    private void fetchRecord() {
        FirebaseDatabase.getInstance().getReference("chats").child(sessionManager.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                               Chat user = snapshot.getValue(Chat.class);
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
}

