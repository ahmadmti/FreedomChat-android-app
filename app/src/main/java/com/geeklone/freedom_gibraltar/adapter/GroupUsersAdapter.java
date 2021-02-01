package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ItemUserBinding;
import com.geeklone.freedom_gibraltar.interfaces.OnUserSelectedListener;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.MembersViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class GroupUsersAdapter extends RecyclerView.Adapter<GroupUsersAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<User> arrayList;
    List<User> arrayListFull;
    Group group;
    boolean isGrouping;
    MembersViewModel viewModel;
    ItemUserBinding binding;
    OnUserSelectedListener listener;
    boolean enableDelete;
    SessionManager sessionManager;

    public GroupUsersAdapter(Context context, List<User> arrayList, Group group, MembersViewModel viewModel, boolean enableDelete, OnUserSelectedListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.group = group;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
        this.isGrouping = isGrouping;
        this.enableDelete = enableDelete;
        this.listener = listener;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_user, parent, false);
        return new MyViewHolder(binding, viewModel);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForColorStateLists"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = arrayList.get(position);
        holder.bind(user, position);

        if (enableDelete) {
            if (group.getCreatedById().equals(sessionManager.getUid())) {
                if (!user.getId().equals(sessionManager.getUid())) {
                    holder.binding.ivUserCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete));
                } else holder.binding.layoutDelMember.setVisibility(View.GONE);
            } else holder.binding.layoutDelMember.setVisibility(View.GONE);
        } else
            holder.binding.ivUserCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle));


        if (enableDelete)
            binding.layoutDelMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = arrayList.get(position);
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete " + user.getName() + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.onUserSelected(user, position);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();


                }
            });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : arrayListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;
        MembersViewModel viewModel;

        public MyViewHolder(ItemUserBinding binding, MembersViewModel viewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel = viewModel;
        }

        public void bind(User item, int position) { // new argument
            binding.setModel(item);
//            binding.setPosition(position); // pass position to the layout
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);
        }
    }
}
