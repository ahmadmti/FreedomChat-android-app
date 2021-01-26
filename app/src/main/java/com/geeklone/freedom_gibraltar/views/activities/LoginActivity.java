package com.geeklone.freedom_gibraltar.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    Context context = this;
//    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
//        binding.setLifecycleOwner(this);
//        binding.setViewModel(viewModel);
//        binding.executePendingBindings();

//        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.navigateTo(LoginActivity.this, MainActivity.class);
//            }
//        });

//        viewModel.getUser().observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(@Nullable User loginUser) {
//                if (loginUser == null) {
//                    Utils.showSnackBar(context, "fill up");
//                    return;
//                }
//
//
//                if (!loginUser.isPasswordValid()) {
//                    Utils.showSnackBar(context, "message");
//
//                } else Utils.showSnackBar(context, "done");
//
//            }
//        });

    }

    public void dologin(View view) {
        Utils.navigateClearTo(context, MainActivityAdmin.class);
    }

    public void dosignupp(View view) {
        Utils.navigateClearTo(context, SignUpActivity.class);
    }


//    @BindingAdapter({"toastMessage"})
//    public static void runMe(View view, String message) {
//        if (message != null)
//            Utils.showSnackBar(view.getContext(), message);
//    }
}