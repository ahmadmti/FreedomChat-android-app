package com.geeklone.freedom_gibraltar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.model.User;
import com.geeklone.freedom_gibraltar.views.activities.EditUserActivity;

import java.util.List;

@SuppressLint("UseSparseArrays")
public class GridAdapter extends BaseAdapter {

    private Context context;
    List<User> userList;

    public GridAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;

    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView;
        User user = userList.get(position);

        if (convertView == null) {
            // get layout from mobile.xml
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.item_member, null);


        } else {
            gridView = convertView;
        }

        AppCompatImageView imageView = gridView.findViewById(R.id.iv_profile_image);
        TextView subTitle = gridView.findViewById(R.id.tv_user_name);

        Utils.loadImage(context, user.getProfileImg(), imageView);
        subTitle.setText(user.getName());

//        switch (model.getSubTitle()) {
//            case Utils.BIO:
//                viewHolder.subImg.setImageResource(R.drawable.img_bio);
//                break;
//
//            case Utils.GENERAL:
////                    viewHolder.subImg.setImageResource(R.drawable.img_c);
//                break;
//
//            case Utils.LOGIC:
//                viewHolder.subImg.setImageResource(R.drawable.img_logica);
//                break;
//
//            case Utils.CHEMISTRY:
//                viewHolder.subImg.setImageResource(R.drawable.img_chimica);
//                break;
//
//            case Utils.PHYSICS:
//                viewHolder.subImg.setImageResource(R.drawable.img_fisica);
//                break;
//
//            case Utils.MATHS:
//                viewHolder.subImg.setImageResource(R.drawable.img_matematica);
//                break;
//        }

        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditUserActivity.class)
                        .putExtra("user", userList.get(position))
                );

            }
        });

        return gridView;
    }

}