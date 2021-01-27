package com.geeklone.freedom_gibraltar.views.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ActivityEditUserBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserActivity extends BaseActivity {

    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);
    ActivityEditUserBinding binding;
    //    EditUserViewModel viewModel;
    MenuItem menuItem;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        loadingDialog = new LoadingDialog(context);

//        viewModel = new ViewModelProvider(this).get(EditUserViewModel.class);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_edit_user, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
//        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        user = (User) getIntent().getSerializableExtra("user");

        Utils.loadImage(context, user.getProfileImg(), binding.ivProfileImage);
        binding.tvUserName.setText(user.getName());
        binding.tvUserEmail.setText(user.getEmail());
        binding.tvUserPhone.setText(user.getMobile());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user, menu);
        menuItem = menu.findItem(R.id.action_update);

        if (user.isEnabled())
            menuItem.setTitle("DEACTIVATE");
        else menuItem.setTitle("ACTIVATE");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_update)
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to change status of " + Utils.getCapsSentence(user.getName()) + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateUserStatus();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        return super.onOptionsItemSelected(item);
    }

    private void updateUserStatus() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference("users").child(user.getId())
                .child("enabled").setValue(!user.isEnabled())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.showToast(context, "Status updated successfully");
                        loadingDialog.dismiss();

                        user.setEnabled(!user.isEnabled());
                        if (user.isEnabled())
                            menuItem.setTitle("DEACTIVATE");
                        else menuItem.setTitle("ACTIVATE");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.showToast(context, "Logging out failed!");
                        loadingDialog.dismiss();
                    }
                });


    }

}