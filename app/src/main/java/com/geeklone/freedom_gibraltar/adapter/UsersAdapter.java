package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.viewmodel.MembersViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<User> arrayList;
    List<User> arrayListFull;
    boolean isGrouping;
    MembersViewModel viewModel;
    ItemUserBinding binding;
    OnUserSelectedListener listener;

    public UsersAdapter(Context context, List<User> arrayList, MembersViewModel viewModel, boolean isGrouping, OnUserSelectedListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
        this.isGrouping = isGrouping;
        this.listener=listener;
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

        if (user.isSelected()) {
            holder.binding.ivUserCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle));
        } else holder.binding.ivUserCheck.setImageDrawable(null);

        holder.binding.itemClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGrouping) {
                    viewModel.startNewChat(v, arrayList.get(position));
                } else {
                    Log.i("TAG", "onClick: ");
                    User user = arrayList.get(position);
                    arrayList.get(position).setSelected(!user.isSelected());
                    notifyDataSetChanged();
                    listener.onUserSelected(  user, position);
                }
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
