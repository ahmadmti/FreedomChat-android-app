package com.geeklone.freedom_gibraltar.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.GroupConversationAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityGroupConversationBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.GroupConversationViewModel;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupConversationActivity extends BaseActivity {

    String TAG = GroupConversationActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);

    ActivityGroupConversationBinding binding;
    GroupConversationViewModel viewModel;
    DataSnapshot userList;
    GroupConversationAdapter adapter;
    User user;
    List<Conversation> arrayList = new ArrayList<>();
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listener();
    }

    private void init() {
        loadingDialog = new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(GroupConversationViewModel.class);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_users);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_group_conversation, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        if (getIntent().hasExtra("group")) {
            group = (Group) getIntent().getSerializableExtra("group");
            super.setupToolbar(group.getName());
            viewModel.init(group);
        }

        adapter = new GroupConversationAdapter(context, arrayList, viewModel);

        viewModel.getConversation().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        FirebaseDatabase.getInstance().getReference().child("chats").child(sessionManager.getUid()).child(user.getId())
//                .child("userInfo").child("msgRead").setValue(true);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                startActivityForResult(new Intent(context, GroupInfoActivity.class)
                        .putExtra("group", group), 200);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }

    }

}