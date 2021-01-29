package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ItemGroupBinding;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Group;
import com.geeklone.freedom_gibraltar.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Group> arrayList;
    List<Group> arrayListFull;

    GroupViewModel viewModel;
    ItemGroupBinding binding;

    public GroupAdapter(Context context, List<Group> arrayList, GroupViewModel viewModel) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_group, parent, false);
        return new GroupAdapter.MyViewHolder(context, binding, viewModel);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForColorStateLists"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Log.i("TAG", "onChanged: " + arrayList.get(position).getName());

        holder.bind(arrayList.get(position), position);
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
            List<Group> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Group item : arrayListFull) {
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
        GroupViewModel viewModel;
        ItemGroupBinding binding;
        Context context;
        SessionManager sessionManager;

        public MyViewHolder(Context context, ItemGroupBinding binding, GroupViewModel viewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel = viewModel;
            this.context = context;
            sessionManager= new SessionManager(context);
        }

        public void bind(Group item, int position) {
            Utils.loadImage(context, item.getGroupImg(), binding.ivProfileImage);

            if (!item.getUpdatedDate().isEmpty()) {
                String msgTime = Utils.formatDateTimeFromTS(Long.parseLong(item.getUpdatedDate()), "hh:mm a");
                item.setMsgDateTime(msgTime);
            }

            item.setLastMsgBy(item.getLastMsgBy().isEmpty() ? "Created by " + item.getCreatedBy()
                    : item.getLastMsgBy().equals(sessionManager.getUserName()) ? "Me: " : item.getLastMsgBy() + ": ");


                binding.setModel(item);
//            binding.setPosition(position); // pass position to the layout
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);

        }
    }
}
