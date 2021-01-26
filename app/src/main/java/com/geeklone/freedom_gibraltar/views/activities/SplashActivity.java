package com.geeklone.freedom_gibraltar.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.helper.Utils;
import com.geeklone.freedom_gibraltar.local.SessionManager;

public class SplashActivity extends AppCompatActivity {

    String TAG = SplashActivity.class.getSimpleName();
    Context context = this;
    SessionManager sessionManager = new SessionManager(this);

    final int SPLASH_TIME_OUT = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Utils.navigateClearTo(context, MainActivityAdmin.class);
//        delayScreen();
    }

    private void delayScreen() {
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIsLoggedIn();
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkIsLoggedIn() {
        if (sessionManager.getIsLoggedIn()) {
            if (sessionManager.getIsAdmin())
                Utils.navigateClearTo(context, MainActivityAdmin.class);
            else Utils.navigateClearTo(context, MainActivity.class);
        } else Utils.navigateClearTo(context, LoginActivity.class);
    }
}