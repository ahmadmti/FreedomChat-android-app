package com.geeklone.freedom_gibraltar.helper;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationService extends FirebaseMessagingService {

    String TAG = NotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            RemoteMessage.Notification object = remoteMessage.getNotification();
            Log.i(TAG, String.valueOf(object));
            assert object != null;
            Utils.createNotification(this, object.getTitle(), object.getBody());
        } catch (Exception ignored) { }
    }
}
