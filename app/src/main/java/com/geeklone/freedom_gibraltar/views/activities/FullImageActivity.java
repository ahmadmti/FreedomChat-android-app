package com.geeklone.freedom_gibraltar.views.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.databinding.ActivityFullImageBinding;
import com.geeklone.freedom_gibraltar.helper.Utils;

import java.io.File;
import java.io.IOException;


public class FullImageActivity extends AppCompatActivity {

    Context context = this;
    ActivityFullImageBinding binding;
    String img, imgName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_image);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        img = getIntent().getStringExtra("img");
        imgName = getIntent().getStringExtra("imgName");

        Utils.loadImage(this, img, binding.expandedImageView);
        binding.expandedImageView.setOnTouchListener(new ImageMatrixTouchHandler(this));

        binding.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImg(img, imgName);
            }
        });
    }


    private void downloadImg(String uri, String imgName) {
        String filename = imgName;
        String downloadUrlOfImage = uri;
        String DIR_NAME = "Freedom";

        File direct = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/" + DIR_NAME + "/");


        if (!direct.exists()) {
            direct.mkdir();
            Log.d("tag", "dir created for first time");
        }

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrlOfImage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + DIR_NAME + File.separator + filename);

        dm.enqueue(request);


    }
}