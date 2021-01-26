package com.geeklone.freedom_gibraltar.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.model.Member;
import com.geeklone.freedom_gibraltar.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MembersViewModel extends AndroidViewModel {

    LoadingDialog loadingDialog;
    private MutableLiveData<List<User>> liveData;
    List<User> arrayList;
    Application application;

    public MembersViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
    }

    public MutableLiveData<List<User>> getMembers() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<User>>();
            loadingDialog= new LoadingDialog(application.getApplicationContext());

            fetchRecord();
        }
        return liveData;
    }

    private void fetchRecord() {
//        loadingDialog.show();
        arrayList= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                               User user = snapshot.getValue(User.class);
                               arrayList.add(user);
                           }

                            liveData.setValue(arrayList);
                        }

//                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("TAG", "Failed to read value.", error.toException());
//                        Utils.showSnackBar(getApplication(), error.getMessage());
//                        loadingDialog.dismiss();
                    }
                });

    }

}

