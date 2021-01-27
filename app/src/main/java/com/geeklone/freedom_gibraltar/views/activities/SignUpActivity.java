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
import com.geeklone.freedom_gibraltar.databinding.ActivitySignUpBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.SignUpViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);
    ActivitySignUpBinding binding;
    SignUpViewModel viewModel;

    private FirebaseAuth mAuth;
    String str_userId;
    private User user;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listener();
        getDeviceToken();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        loadingDialog= new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User loginUser) {
                user = loginUser;
                registerNewUser();
            }
        });

    }


    private void listener() {
        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateTo(context, LoginActivity.class);
            }
        });
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


    private void registerNewUser() {
        loadingDialog.show();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            str_userId = mAuth.getCurrentUser().getUid();
                            createNewUser(makePayload());
                            Utils.showToast(context, "Registration successful!");
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private Map<String, Object> makePayload() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", str_userId);
        hashMap.put("name", user.getName());
        hashMap.put("mobile", user.getMobile());
        hashMap.put("email", user.getEmail());
        hashMap.put("password", user.getPassword());
        hashMap.put("deviceToken", deviceToken);
        hashMap.put("profileImg", "");
        hashMap.put("enabled", true);
        hashMap.put("admin", false);
        hashMap.put("createdDate", Utils.formatDateTimeFromTS(Utils.getSysTimeStamp(), "yyyy-MM-dd HH:mm:ss"));

        return hashMap;
    }


    private void createNewUser(Map<String, Object> payload) {
        FirebaseDatabase.getInstance().getReference("users").child(str_userId)
                .setValue(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                        saveLocalDetails();
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

    private void saveLocalDetails() {
        sessionManager.setUid(mAuth.getCurrentUser().getUid()); //reg user id
        sessionManager.setUsername( user.getName());
        sessionManager.setUserMobile(user.getMobile());
        sessionManager.setUserEmail( user.getEmail());
        sessionManager.setUserPwd(user.getPassword());
        sessionManager.setDeviceToken(deviceToken);
        sessionManager.setIsAdmin(false);
        sessionManager.setIsLoggedIn(true);

        Utils.navigateClearTo(context, MainActivity.class);
    }
}