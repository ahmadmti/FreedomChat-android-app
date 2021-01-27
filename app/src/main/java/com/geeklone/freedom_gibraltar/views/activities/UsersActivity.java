package com.geeklone.freedom_gibraltar.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.MembersViewModel;
import com.geeklone.freedom_gibraltar.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity {

    String TAG = UsersActivity.class.getSimpleName();
    Context context = this;
    LoadingDialog loadingDialog;
    SessionManager sessionManager = new SessionManager(this);

    ActivityUsersBinding binding;
    MembersViewModel viewModel;
    List<User> userList = new ArrayList<>();
    UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupToolbar("Users");
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

        loadingDialog.show();
        viewModel.getMembers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                loadingDialog.dismiss();
                userList = users;
                if (userList.size() > 0) {
                    adapter = new UsersAdapter(context, userList, viewModel);
                    binding.rvUsers.setAdapter(adapter);
                } else binding.tvNotFound.setVisibility(View.VISIBLE);
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
}