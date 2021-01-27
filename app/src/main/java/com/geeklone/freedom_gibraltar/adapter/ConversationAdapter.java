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
import com.geeklone.freedom_gibraltar.databinding.ActivityConversationBinding;
import com.geeklone.freedom_gibraltar.databinding.ItemConversationBinding;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.viewmodel.ConversationViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Conversation> arrayList;
    List<Conversation> arrayListFull;

    ConversationViewModel viewModel;
    ItemConversationBinding binding;

    public ConversationAdapter(Context context, List<Conversation> arrayList, ConversationViewModel viewModel) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_conversation, parent, false);
        return new ConversationAdapter.MyViewHolder(binding, viewModel);
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
        ConversationViewModel viewModel;
        ItemConversationBinding binding;

        public MyViewHolder(ItemConversationBinding binding,  ConversationViewModel viewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel=viewModel;
        }

        public void bind(Conversation item, int position) { // new argument
            binding.setModel(item);
//            binding.setPosition(position); // pass position to the layout
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);

        }
    }
}
