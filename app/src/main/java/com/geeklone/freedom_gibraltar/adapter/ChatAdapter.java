package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ItemChatBinding;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.model.Chat;
import com.geeklone.freedom_gibraltar.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Chat> arrayList;
    List<Chat> arrayListFull;

    ChatViewModel viewModel;
    ItemChatBinding binding;

    public ChatAdapter(Context context, List<Chat> arrayList, ChatViewModel viewModel) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_chat, parent, false);
        return new ChatAdapter.MyViewHolder(binding, viewModel);
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
            List<Chat> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Chat item : arrayListFull) {
                    if (item.getChatUserName().toLowerCase().contains(filterPattern)) {
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
        ChatViewModel viewModel;
        ItemChatBinding binding;

        public MyViewHolder(ItemChatBinding binding, ChatViewModel viewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel = viewModel;
        }

        public void bind(Chat item, int position) {
            if (!item.getUpdatedDate().isEmpty()) {
                String msgTime = Utils.formatDateTimeFromTS(Long.parseLong(item.getUpdatedDate()), "hh:mm a");
                item.setMsgDateTime(msgTime);
            }
            binding.setModel(item);
//            binding.setPosition(position); // pass position to the layout
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);

        }
    }
}
