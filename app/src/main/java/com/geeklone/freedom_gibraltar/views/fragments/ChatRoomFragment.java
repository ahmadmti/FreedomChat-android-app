package com.geeklone.freedom_gibraltar.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.adapter.ChatAdapter;
import com.geeklone.freedom_gibraltar.databinding.FragmentChatRoomBinding;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Chat;
import com.geeklone.freedom_gibraltar.viewmodel.ChatViewModel;
import com.geeklone.freedom_gibraltar.views.activities.LoginActivity;
import com.geeklone.freedom_gibraltar.views.activities.UsersActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ChatRoomFragment extends Fragment {

    String TAG = ChatRoomFragment.class.getSimpleName();
    SessionManager sessionManager;
    LoadingDialog loadingDialog;
    List<Chat> chatList = new ArrayList<>();
    String date, customerCode, route;

    FragmentChatRoomBinding binding;
    ChatViewModel viewModel;
    ChatAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false);
        setHasOptionsMenu(true); //toolbar item click
        init();
        listener();

        return binding.getRoot();
    }


    private void init() {
        sessionManager = new SessionManager(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        binding.rvChat.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)); //divider

        loadingDialog.show();
        viewModel.getChats().observe(getViewLifecycleOwner(), new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> chats) {
                loadingDialog.dismiss();
                chatList = chats;
                if (chatList.size() > 0) {
                    adapter = new ChatAdapter(getContext(), chatList, viewModel);
                    binding.rvChat.setAdapter(adapter);
                } else binding.tvNotFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private void listener() {
        binding.fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateTo(getContext(), UsersActivity.class);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doLogout();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUid())
                .child("deviceToken").setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sessionManager.setIsLoggedIn(false);
                        sessionManager.setIsAdmin(false);
                        loadingDialog.dismiss();
                        Utils.navigateClearTo(getContext(), LoginActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.showToast(getContext(), "Logging out failed!");
                        loadingDialog.dismiss();
                    }
                });
    }

}