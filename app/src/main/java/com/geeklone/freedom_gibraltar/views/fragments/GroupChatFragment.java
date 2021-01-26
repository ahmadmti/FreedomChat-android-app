package com.geeklone.freedom_gibraltar.views.fragments;

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
import androidx.fragment.app.Fragment;

import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;


public class GroupChatFragment extends Fragment /*implements RetrofitRespondListener*/ {


    String TAG = GroupChatFragment.class.getSimpleName();
    SessionManager sessionManager;
    LoadingDialog loadingDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_group_chat, container, false);
        setHasOptionsMenu(true); //toolbar item click
        init();

        return root;
    }





    private void init() {
        loadingDialog = new LoadingDialog(getContext());
        sessionManager = new SessionManager(getContext());


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

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
//                    if (adapter != null) adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
    }

}