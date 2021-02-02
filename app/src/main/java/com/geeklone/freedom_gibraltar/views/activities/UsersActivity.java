package com.geeklone.freedom_gibraltar.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.UsersAdapter;
import com.geeklone.freedom_gibraltar.databinding.ActivityUsersBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.interfaces.OnUserSelectedListener;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.MembersViewModel;
import com.geeklone.freedom_gibraltar.views.BaseActivity;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UsersActivity extends BaseActivity implements OnUserSelectedListener {

    String TAG = UsersActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);

    ActivityUsersBinding binding;
    MembersViewModel viewModel;
    List<User> userList = new ArrayList<>();
    List<User> selectedUserList;
    UsersAdapter adapter;
    boolean isGrouping = false;
    Group group;
    List<User> addedUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupToolbar("New Group (" + "0" + " members)");
        init();
        listener();
    }

    private void init() {
        loadingDialog = new LoadingDialog(context);

        viewModel = new ViewModelProvider(this).get(MembersViewModel.class);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_users);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_users, (FrameLayout) findViewById(R.id.baseActToolbar_frame), true);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        binding.rvUsers.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL)); //divider

        if (getIntent().hasExtra("makeNewGroup") || getIntent().hasExtra("addMemberToGroup")) {
            binding.fabCreateGroup.setVisibility(View.VISIBLE);
            isGrouping = true;
        }

        if (getIntent().hasExtra("addMemberToGroup")) {
            addedUserList = (List<User>) getIntent().getSerializableExtra("userList");
            group = (Group) getIntent().getSerializableExtra("group");
        }

        loadingDialog.show();
        viewModel.getMembers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                loadingDialog.dismiss();
                userList = users;

                if (userList.size() > 0) {
                    List<User> nonAddedUserList = new ArrayList<>();

                    //todo
                    if (getIntent().hasExtra("addMemberToGroup")) {
                        List<String> fullStr = new ArrayList<>();
                        List<String> addedStr = new ArrayList<>();
                        List<String> diffStr = new ArrayList<>();

                        for (User user : userList)
                            fullStr.add(user.getId());

                        for (User user : addedUserList)
                            addedStr.add(user.getId());

                        for (String s : fullStr)
                            if (!addedStr.contains(s))
                                diffStr.add(s);

                        for (String s : diffStr)
                            for (User user : userList)
                                if (user.getId().equals(s))
                                    nonAddedUserList.add(user);

                        adapter = new UsersAdapter(context, nonAddedUserList, addedUserList, viewModel, isGrouping, UsersActivity.this);

                    } else
                        adapter = new UsersAdapter(context, userList, addedUserList, viewModel, isGrouping, UsersActivity.this);


//                    for (User user : nonAddedUserList) {
//                        Log.i(TAG, "onChanged: " + user.getName());
//                    }


                    binding.rvUsers.setAdapter(adapter);
                } else binding.tvNotFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private void listener() {
        binding.fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("makeNewGroup"))
                    startActivityForResult(new Intent(context, CreateGroupActivity.class)
                            .putExtra("userList", (Serializable) selectedUserList), 100);
                else addSelectedMembers(makePayload());
            }
        });
    }

    private Map<String, Object> makePayload() {
        List<String> memberList = new ArrayList<>();
        for (User user : addedUserList) {
            memberList.add(user.getId());
        }

        if (selectedUserList != null)
            for (User user : selectedUserList) {
                memberList.add(user.getId());
            }


        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("members", memberList);

        return hashMap;
    }

    private void addSelectedMembers(Map<String, Object> payload) {
        FirebaseDatabase.getInstance().getReference("groups").child(group.getId()).child("groupInfo")
                .updateChildren(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                        Utils.showToast(context, "Updated successfully!");
                        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(Activity.RESULT_OK, new Intent());
                                finish();
                            }
                        }, 500);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search here...");
        searchView.setBackground(null);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter != null) adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }

        return true;

    }

    @Override
    public void onUserSelected(User user, int position) {
        selectedUserList = new ArrayList<>();
        int selectedCount = 0;
        for (User user1 : userList) {
            if (user1.isSelected()) {
                selectedUserList.add(user1);
                selectedCount = selectedCount + 1;
            }
        }

        super.setupToolbar("New Group (" + selectedCount + " members)");

        if (selectedUserList.size() > 0)
            binding.fabCreateGroup.setVisibility(View.VISIBLE);
        else binding.fabCreateGroup.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: ");
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }

    }

}