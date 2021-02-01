package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.geeklone.freedom_gibraltar.databinding.ItemConversationBinding;
import com.geeklone.freedom_gibraltar.generated.callback.OnClickListener;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.geeklone.freedom_gibraltar.model.Conversation;
import com.geeklone.freedom_gibraltar.viewmodel.ConversationViewModel;
import com.geeklone.freedom_gibraltar.views.activities.FullImageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * developed by irfan A.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Conversation> arrayList;
    List<Conversation> arrayListFull;
    SessionManager sessionManager;
    ConversationViewModel viewModel;
    ItemConversationBinding binding;
    static String date;

    public ConversationAdapter(Context context, List<Conversation> arrayList, ConversationViewModel viewModel) {
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_conversation, parent, false);
        return new ConversationAdapter.MyViewHolder(context, binding, viewModel, sessionManager);
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
        SessionManager sessionManager;
        Context context;

        public MyViewHolder(Context context, ItemConversationBinding binding, ConversationViewModel viewModel, SessionManager sessionManager) {
            super(binding.getRoot());
            this.binding = binding;
            this.viewModel = viewModel;
            this.sessionManager = sessionManager;
            this.context = context;
        }

        public void bind(Conversation item, int position) { // new argument
            if (item.getFrom().equals(sessionManager.getUid())) {
                item.setSending(true);
                if (item.getMsgType().equals("img"))
                    Utils.loadImage(context, item.getMsg(), binding.ivSenderUserImg);
            } else {
                if (item.getMsgType().equals("img"))
                    Utils.loadImage(context, item.getMsg(), binding.ivRecieverUserImg);
            }
            String msgTime = Utils.formatDateTimeFromTS(Long.parseLong(item.getTimeStamp()), "hh:mm a");
            String conversationDate = Utils.formatDateTimeFromTS(Long.parseLong(item.getTimeStamp()), "MMM dd, yyyy");

//            if (date == null) {
//                item.setConversationDateVisibility(true);
//                Log.i("TAG", "null: ");
//
//            } else {
//                if (date.equals(conversationDate)) {
//                    item.setConversationDateVisibility(false);
//                    Log.i("TAG", "iff: ");
//                } else {
//                    item.setConversationDateVisibility(true);
//                    Log.i("TAG", "else: ");
//
//                }
//
//            }

            date = conversationDate;

            item.setConversationDate(conversationDate);
            item.setMsgTime(msgTime);


            binding.ivSenderUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FullImageActivity.class)
                            .putExtra("img", item.getMsg())
                            .putExtra("imgName", item.getImgName()));
                }
            });

            binding.ivRecieverUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FullImageActivity.class)
                            .putExtra("img", item.getMsg())
                            .putExtra("imgName", item.getImgName()));
                }
            });


            binding.setModel(item);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
//            binding.setLifecycleOwner(this);

        }
    }
}
