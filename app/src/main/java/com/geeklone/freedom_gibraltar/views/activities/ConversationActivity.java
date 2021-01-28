package com.geeklone.freedom_gibraltar.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.ConversationAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityConversationBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.ConversationViewModel;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.google.firebase.database.DataSnapshot;

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

        user = (User) getIntent().getSerializableExtra("user");
        super.setupToolbar(user.getName());
        viewModel.init(user);

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

}