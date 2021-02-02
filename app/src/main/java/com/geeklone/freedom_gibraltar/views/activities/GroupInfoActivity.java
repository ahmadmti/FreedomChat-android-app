package com.geeklone.freedom_gibraltar.views.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.GroupUsersAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityGroupInfoBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.interfaces.OnUserSelectedListener;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInfoActivity extends BaseActivity implements OnUserSelectedListener {

    String TAG = GroupConversationActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);

    ActivityGroupInfoBinding binding;
    GroupUsersAdapter adapter;
    User user;
    List<Conversation> arrayList = new ArrayList<>();
    Group group;
    String str_groupName;
    String str_key;
    Uri resultUri;
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupToolbar("Group Info");
        init();
        listener();
        fetchMembers();
    }


    @SuppressLint("SetTextI18n")
    private void init() {
        loadingDialog = new LoadingDialog(context);

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_group_info, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

//        binding.rvGroupMembers.setAdapter(new GroupUsersAdapter(context, userList, null, null));
        group = (Group) getIntent().getSerializableExtra("group");

        Utils.loadImage(context, group.getGroupImg(), binding.ivGroupImage);
        binding.ivGroupImage.setPadding(0, 0, 0, 0);
        binding.etGroupName.setText(group.getName());
        binding.tvGroupMembersCount.setText(group.getMemberCount() + " members");
    }

    private void fetchMembers() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (group.getMembers().contains(snapshot.child("id").getValue(String.class))) {
                                    User user = snapshot.getValue(User.class);
                                    userList.add(user);
                                }
                            }

                            if (userList.size() > 0) {
                                adapter = new GroupUsersAdapter(context, userList, group, null, true, GroupInfoActivity.this);
                                binding.rvGroupMembers.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });

    }


    private void listener() {
        binding.ivGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(GroupInfoActivity.this);
            }
        });

        binding.layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, UsersActivity.class)
                                .putExtra("addMemberToGroup", true)
                                .putExtra("userList", (Serializable) userList)
                                .putExtra("group", group),
                        300
                );
            }
        });

        binding.layoutDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete " + group.getName() + " permanently?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                FirebaseDatabase.getInstance().getReference().child("groups").child(group.getId()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loadingDialog.dismiss();
                                                Utils.showToast(context, "Group Deleted!");
                                                setResult(Activity.RESULT_OK, new Intent());
                                                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finish();
                                                    }
                                                }, 500);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                str_groupName = binding.etGroupName.getText().toString().trim();
                if (str_groupName.isEmpty()) {
                    binding.etGroupName.setError("required");
                } else uploadImage(resultUri);

                break;
        }

        return super.onOptionsItemSelected(item);

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
//                binding.ivGroupImage.setImageURI(resultUri);
                Bitmap bitmap ;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    binding.ivGroupImage.setImageBitmap(bitmap);
                    binding.ivGroupImage.setPadding(0, 0, 0, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Utils.showSnackBar(this, String.valueOf(result.getError()));
            }
        } else if (requestCode == 300) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();

            }
        }
    }


    private void uploadImage(Uri uri) {
        loadingDialog.show();
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("groups").child(group.getId())
                    .child("grp_img" + "." + Utils.getFileExtension(context, uri)); //root path

            mStorageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateGroup(makePayload(uri));
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Utils.showSnackBar(context, e.getMessage());
                            Log.e("TAG", "onFailure: " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });

        } else updateGroup(makePayload(null));
    }

    private Map<String, Object> makePayload(Uri groupImgUri) {

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", str_groupName);

        if (groupImgUri != null)
            hashMap.put("groupImg", String.valueOf(groupImgUri));

        return hashMap;
    }


    private void updateGroup(Map<String, Object> payload) {
        FirebaseDatabase.getInstance().getReference("groups").child(group.getId()).child("groupInfo")
                .updateChildren(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                        Utils.showToast(context, "Updated successfully!");
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
    public void onUserSelected(User user, int position) {
        Log.i(TAG, "onUserSelected: " + position);
        deleteUser(user, position);

    }

    private void deleteUser(User user, int position) {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference("groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            for (DataSnapshot snapshotMain : dataSnapshot.getChildren()) {
                                DataSnapshot snapshot = snapshotMain.child("groupInfo");
                                for (DataSnapshot snapshot1 : snapshot.child("members").getChildren()) {
                                    if (snapshot1.getValue(String.class).equals(user.getId())) {
                                        snapshot1.getRef().removeValue();
                                        userList.remove(position);
                                        adapter.notifyDataSetChanged();
                                        binding.tvGroupMembersCount.setText(group.getMemberCount() + " members");
                                        break;
                                    }
                                }
                            }
                        }

                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        loadingDialog.dismiss();
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });


    }
}