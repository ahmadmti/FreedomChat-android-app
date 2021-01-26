package com.geeklone.freedom_gibraltar.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ActivityLoginBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    String TAG = LoginActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);
    ActivityLoginBinding binding;
    LoginViewModel viewModel;

    private FirebaseAuth mAuth;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listener();
        getDeviceToken();
    }

    private void getDeviceToken() {
        //get device token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(this, instanceIdResult -> {
                    deviceToken = instanceIdResult.getToken();
                    Log.e("device_token", deviceToken);
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("device_token", e.getMessage());
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        loadingDialog= new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                loginUserAccount(user.getEmail(), user.getPassword());
            }
        });

    }

    private void listener() {
        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateTo(context, SignUpActivity.class);
            }
        });
    }


    private void loginUserAccount(String email, String password) {
        loadingDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserDetails();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            if (!dataSnapshot.child("isEnabled").getValue(Boolean.class)) {
                                Utils.showToast(context, "Account disabled! Please contact to the administrator");
                            } else {
                                dataSnapshot.getRef().child("deviceToken").setValue(deviceToken); //add device token on login
                                User user = dataSnapshot.getValue(User.class);
                                saveLocalDetails(user);
                            }
                        }

                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                        Utils.showSnackBar(context, error.getMessage());
                        loadingDialog.dismiss();
                    }
                });
    }

    private void saveLocalDetails(User user ) {
        sessionManager.setUid(mAuth.getCurrentUser().getUid()); //reg user id
        sessionManager.setUsername( user.getName());
        sessionManager.setUserMobile(user.getMobile());
        sessionManager.setUserEmail( user.getEmail());
        sessionManager.setUserPwd(user.getPassword());
        sessionManager.setUserImg(user.getEmail());
        sessionManager.setDeviceToken(deviceToken);
        sessionManager.setIsAdmin(user.isAdmin());
        sessionManager.setIsLoggedIn(true);

//        if (user.isAdmin())
            Utils.navigateClearTo(context, MainActivityAdmin.class);
//        else Utils.navigateClearTo(context, MainActivity.class);

    }
}