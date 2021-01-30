package com.geeklone.freedom_gibraltar.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.GroupUsersAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityCreateGroupBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.interfaces.OnUserSelectedListener;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends BaseActivity implements OnUserSelectedListener {

    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);
    ActivityCreateGroupBinding binding;
    String str_groupName;
    String str_key;
    List<User> userList;
    Uri resultUri;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupToolbar("Set Group Info");
        init();
        listener();
    }


    private void init() {
        loadingDialog = new LoadingDialog(context);

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_create_group, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        userList = (List<User>) getIntent().getSerializableExtra("userList");
        binding.rvGroupMembers.setAdapter(new GroupUsersAdapter(context, userList, null, false, CreateGroupActivity.this));
    }


    private void listener() {
        binding.ivGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(CreateGroupActivity.this);
            }
        });

        binding.fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_groupName = binding.etGroupName.getText().toString().trim();
                if (str_groupName.isEmpty()) {
                    binding.etGroupName.setError("required");
                } else uploadImage(null);

            }
        });
    }

    private Map<String, Object> makePayload(Uri groupImgUri) {
        List<String> memberList = new ArrayList<>();
        memberList.add(sessionManager.getUid());
        for (User user : userList) {
            memberList.add(user.getId());
        }

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", str_key);
        hashMap.put("name", str_groupName);
        hashMap.put("groupImg", groupImgUri);
        hashMap.put("memberCount", String.valueOf(memberList.size()));
        hashMap.put("createdDate", Utils.formatDateTimeFromTS(Utils.getSysTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        hashMap.put("createdBy", sessionManager.getUserName());
        hashMap.put("members", memberList);

        return hashMap;
    }


    private void createNewGroup(Map<String, Object> payload) {
//
//        reference.child(str_key).child("members").setValue(sessionManager.getUid()); //who is creating grp
//        for (User user : userList) {
//            reference.child(str_key).child("members").setValue(user.getId());
//        }

        reference.child(str_key).child("groupInfo")
                .setValue(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        Utils.showSnackBar(context, e.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ImagePicker result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                binding.ivGroupImage.setImageURI(resultUri);
                binding.ivGroupImage.setPadding(0, 0, 0, 0);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Utils.showSnackBar(this, String.valueOf(result.getError()));
            }
        }
    }

    private void uploadImage(Uri uri) {
        reference = FirebaseDatabase.getInstance().getReference("groups");
        str_key = reference.push().getKey();

        loadingDialog.show();
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("groups").child(str_key)
                    .child("grp_img" + "." + Utils.getFileExtension(context, uri)); //root path

            mStorageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    createNewGroup(makePayload(uri));
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.showSnackBar(context, e.getMessage());
                            loadingDialog.dismiss();
                            Log.e("TAG", "onFailure: " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });

        } else createNewGroup(makePayload(null));
    }


    @Override
    public void onUserSelected(User user, int position) {

    }
}