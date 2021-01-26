package com.geeklone.freedom_gibraltar.views.fragments;

import android.annotation.SuppressLint;
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

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.helper.LoadingDialog;
import com.geeklone.freedom_gibraltar.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;


public class ChatRoomFragment extends Fragment {

    String TAG = ChatRoomFragment.class.getSimpleName();
    SessionManager sessionManager;
    LoadingDialog loadingDialog;
    List<Subject> arrSub = new ArrayList<>();
    String date, customerCode, route;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat_room, container, false);
        setHasOptionsMenu(true); //toolbar item click
        init();
//        fetchSubjects();

        return root;
    }

    //
    @SuppressLint("SetTextI18n")
    private void init() {
        sessionManager = new SessionManager(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
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