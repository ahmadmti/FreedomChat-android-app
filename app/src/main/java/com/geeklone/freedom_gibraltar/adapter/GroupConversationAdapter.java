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
import com.geeklone.freedom_gibraltar.databinding.ItemConversationBinding;
import com.geeklone.freedom_gibraltar.databinding.ItemGroupConversationBinding;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.viewmodel.GroupConversationViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class GroupConversationAdapter extends RecyclerView.Adapter<GroupConversationAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Conversation> arrayList;
    List<Conversation> arrayListFull;
    SessionManager sessionManager;
    GroupConversationViewModel viewModel;
    ItemGroupConversationBinding binding;
    static String date;

    public GroupConversationAdapter(Context context, List<Conversation> arrayList, GroupConversationViewModel viewModel) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_group_conversation, parent, false);
        return new GroupConversationAdapter.MyViewHolder(binding, viewModel, sessionManager);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForColorStateLists"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
            List<Conversation> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Conversation item : arrayListFull) {
                    if (item.getMsg().toLowerCase().contains(filterPattern)) {
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
        GroupConversationViewModel viewModel;
        ItemGroupConversationBinding binding;
        SessionManager sessionManager;

        public MyViewHolder(ItemGroupConversationBinding binding, GroupConversationViewModel viewModel, SessionManager sessionManager) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel = viewModel;
            this.sessionManager = sessionManager;
        }

        public void bind(Conversation item, int position) { // new argument
            if (item.getFrom().equals(sessionManager.getUid())) item.setSending(true);
            String msgTime = Utils.formatDateTimeFromTS(Long.parseLong(item.getTimeStamp()), "hh:mm a");
            String conversationDate = Utils.formatDateTimeFromTS(Long.parseLong(item.getTimeStamp()), "MMM dd, yyyy");




            if (date == null) {
                item.setConversationDateVisibility(true);
                Log.i("TAG", "null: " );

            } else {
                if (date.equals(conversationDate)) {
                    item.setConversationDateVisibility(false);
                    Log.i("TAG", "iff: " );
                } else {
                    item.setConversationDateVisibility(true);
                    Log.i("TAG", "else: " );

                }
            }

            date = conversationDate;

            item.setConversationDate(conversationDate);
            item.setMsgTime(msgTime);

            binding.setModel(item);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);
        }
    }
}
