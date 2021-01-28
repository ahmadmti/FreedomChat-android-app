package com.geeklone.freedom_gibraltar.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ActivityCreateGroupBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.CreateGroupViewModel;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);
    ActivityCreateGroupBinding binding;
    CreateGroupViewModel viewModel;

    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listener();
    }


    private void init() {
        loadingDialog= new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(CreateGroupViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        userList = (List<User>) getIntent().getSerializableExtra("userList");

        viewModel.getName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String name) {

            }
        });

    }


    private void listener() {
//        binding.fabCreateGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }
}