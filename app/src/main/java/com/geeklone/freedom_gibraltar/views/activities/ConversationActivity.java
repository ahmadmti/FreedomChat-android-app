package com.geeklone.freedom_gibraltar.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.ConversationAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityConversationBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Chat;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.ConversationViewModel;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends BaseActivity {

    String TAG = ConversationActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);

    ActivityConversationBinding binding;
    ConversationViewModel viewModel;
    DataSnapshot userList;
    ConversationAdapter adapter;
    User user;
    List<Conversation> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listener();
    }

    private void init() {
        loadingDialog = new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_users);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_conversation, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        if (getIntent().hasExtra("chat")) {
            Chat chat = (Chat) getIntent().getSerializableExtra("chat");
            super.setupToolbar(chat.getChatUserName());
            FirebaseDatabase.getInstance().getReference("users").child(chat.getChatUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.i(TAG, "onDataChange: "+ snapshot);
                            user = snapshot.getValue(User.class);
                            viewModel.init(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        } else {
            user = (User) getIntent().getSerializableExtra("user");
            super.setupToolbar(user.getName());
            viewModel.init(user);
        }

        adapter = new ConversationAdapter(context, arrayList, viewModel);

//        loadingDialog.show();
        viewModel.getConversation().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
//                loadingDialog.dismiss();

                if (dataSnapshot != null)
                    updateConversation(dataSnapshot);
            }
        });

    }

    private void listener() {
//        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.navigateTo(context, LoginActivity.class);
//            }
//        });
    }

    public void updateConversation(DataSnapshot dataSnapshot) {
        Conversation conversation = dataSnapshot.getValue(Conversation.class);
        arrayList.add(conversation);

        binding.rvConversation.setAdapter(adapter);
//        recyclerView.setSelection(adapter.getCount() - 1);

        adapter.notifyDataSetChanged();
        binding.rvConversation.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference().child("chats").child(sessionManager.getUid()).child(user.getId())
                .child("userInfo").child("msgRead").setValue(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ImagePicker result
        if (resultCode == Activity.RESULT_OK) {

            if (data.getData() != null) {
                loadingDialog.show();
                //uploading the file
                Uri   fileUri = data.getData();
                Log.e(TAG, "onActivityResult: "+ fileUri );

                String uriString = fileUri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(fileUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

                uploadImage(fileUri, displayName);

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Utils.showSnackBar(context, ImagePicker.Companion.getError(data));
        }
    }

    private void uploadImage(Uri uri, String displayName) {
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("chats/images")
                    .child(String.valueOf(Utils.getSysTimeStamp())); //root path

            mStorageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loadingDialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.setAction("getImgURI");
                                    intent.putExtra("URI", String.valueOf(uri));
                                    intent.putExtra("name", String.valueOf(displayName));
                                    sendBroadcast(intent);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.showSnackBar(context, e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });

        }
    }

}