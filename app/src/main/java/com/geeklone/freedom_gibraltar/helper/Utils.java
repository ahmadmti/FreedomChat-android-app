package com.geeklone.freedom_gibraltar.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geeklone.freedom_gibraltar.views.activities.MainActivity;
import com.geeklone.freedom_gibraltar.R;
import com.geeklone.freedom_gibraltar.local.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * developed by Irfan A.
 */

public class Utils {

    public static final String PENDING = "1";
    public static final String IN_PROCESS = "2";
    public static final String CONFIRMED = "3";
    public static final String COMPLETED = "4";
    public static final String CANCELLED = "5";

    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z-]+\\.+[a-z]+";
    public static String FIREBASE_SERVER_KEY = "AAAA043daFo:APA91bEELXiaPKuHpOg5BqUW4A02FXrheoRNpc1HhwhNbbVX3pTAO5gmAEuDyVSm7cH87iD1m-KgpChG1hR-noFCJX1BrX0CuZ7FifAcwI4nKM_2FA8425yJupBnzIjX_oCnNq0_Wt69";


    /**
     * show toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * show snackBar
     *
     * @param context
     * @param msg
     */
    public static void showSnackBar(Context context, String msg) {
        View view = ((Activity) context).findViewById(android.R.id.content);
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * navigate to desire activity
     *
     * @param context
     * @param destination
     */
    public static void navigateTo(Context context, Class<?> destination) {
        context.startActivity(new Intent(context, destination));
    }

    /**
     * navigate to desire activity with NEW_TASK
     *
     * @param context
     * @param destination
     */
    public static void navigateClearTo(Context context, Class<?> destination) {
        context.startActivity(new Intent(context, destination)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }


    /**
     * hide keypad
     *
     * @param activity
     */
    public static void hideKeypad(Activity activity, boolean flag) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        if (flag) imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //hide
        else imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); //show

    }

    /**
     * populate profile image
     *
     * @param context
     * @param imgURL
     * @param placeHolder
     */
    @SuppressLint("CheckResult")
    public static void loadProfileImage(Context context, String imgURL, CircleImageView placeHolder) {
        if (imgURL == null) return;

        Glide.with(context)
                .load(imgURL)
                .placeholder(R.drawable.img_place_holder)
                .error(R.drawable.img_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .override(100, 200)
//                .centerCrop()
//                .fitCenter() // scale to fit entire image within ImageView
                .into(placeHolder);
    }

    /**
     * populate image
     *
     * @param context
     * @param imgURL
     * @param placeHolder
     */
    @SuppressLint("CheckResult")
    public static void loadImage(Context context, String imgURL, AppCompatImageView placeHolder) {
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(4f);
        progressDrawable.setCenterRadius(20f);
        progressDrawable.start();

        if (imgURL == null)
            return;

        Glide.with(context)
                .load( imgURL)
                .placeholder(progressDrawable)
                .error(R.drawable.img_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .override(100, 200)
//                .centerCrop()
//                .fitCenter() // scale to fit entire image within ImageView
                .into(placeHolder);
    }

    /**
     * full screen
     *
     * @param activity
     */
    public static void setToFullScreen(Activity activity) { //set to full screen before showing to user
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * change status bar color
     *
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(Activity activity, int color) {
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
    }

    /**
     * change system nav bar color
     *
     * @param activity
     * @param color
     */
    public static void setSysNavigationBarColor(Activity activity, int color) {
        activity.getWindow().setNavigationBarColor(activity.getResources().getColor(color));
    }


    /**
     * @param status
     * @return
     */
    public static String getOrderStatus(String status) {
        if (status.equals(Utils.PENDING))
            return "Pending";
        else if (status.equals(Utils.IN_PROCESS))
            return "In process";
        else if (status.equals(Utils.CONFIRMED))
            return "Confirmed";
        else if (status.equals(Utils.COMPLETED))
            return "Completed";
        else if (status.equals(Utils.CANCELLED))
            return "Cancelled";
        else return "";
    }

    /**
     * get system time stamp
     *
     * @return
     */
    public static long getSysTimeStamp() {
        return System.currentTimeMillis();
    }


    /**
     * format time stamp
     *
     * @param timestamp
     * @param outputFormat
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDateTimeFromTS(long timestamp, String outputFormat) {
        Date time = new Date(timestamp);
        DateFormat outPut = new SimpleDateFormat(outputFormat);
        //Hear Define your returning date formate
        return outPut.format(time);
    }

    public static String formatDateTimeFromString(String inputString, String inputStringFormat, String outputFormat) {
        SimpleDateFormat format = new SimpleDateFormat(inputStringFormat);
        Date newDate = null;
        try {
            newDate = format.parse(inputString);

        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat(outputFormat);

        return format.format(newDate);
    }

    /**
     * create notification
     *
     * @param context
     */
    public static void createNotification(Context context, String title, String body) {
        Uri alertTune = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0); //add action on tap on createNotification

        String CHANNEL_ID = "1234";
        String CHANNEL_NAME = "channel_01";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH); //create createNotification cahnnel for Oreo and above
            channel.setLightColor(Color.GRAY);
            channel.enableLights(true);
            channel.setDescription("");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(alertTune, audioAttributes);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        //create notification
        NotificationCompat.Builder status = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSound(alertTune)
//                .setStyle(new NotificationCompat.InboxStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        int NOTIFICATION_ID = 1; // Causes to update the same notification over and over again.
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, status.build());
        }
    }

    /**
     * share app
     *
     * @param context
     */
    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        String shareMessage = "Let me recommend you this application\n\n";
//        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static String addDays(int days) {
        String dateInString = formatDateTimeFromTS(System.currentTimeMillis(), "yyyy-MM-dd");  // current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateInString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, days);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date resultDate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(resultDate);

        return dateInString;
    }


    public static boolean isTodaysDate(String selectedDate) {
//        String endDate = "21-10-2019";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentSysDate = Calendar.getInstance().getTime(); //get current sys date
        String currentDate = dateFormatter.format(currentSysDate); //first format and get String val
//        String selectedFormattedDate = dateFormatter.format(selectedDate); //first format and get String val

        boolean dateFlag = false;

        try {
//            Log.i("cDate>>", currentDate);
//            Log.i("sDate>>", String.valueOf(dateFormatter.parse(selectedDate)));

            //                Log.i("Date>>", "true");
            // If selected date is after the current date.
            //                Log.i("Date>>", "false");
            if (dateFormatter.parse(selectedDate).before(dateFormatter.parse(currentDate))) {
                dateFlag = false;  // If selected date is before current date.
//                Log.i("Date>>", "true");

            } else
                dateFlag = dateFormatter.parse(selectedDate).equals(dateFormatter.parse(currentDate));  // If two dates are equal.
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFlag;
    }

    /**
     * do capital every 1st letter of the word
     *
     * @param text
     * @return
     */
    public static String getCapsSentence(String text) {
        if (text.isEmpty())
            return "";

        String[] splits = text.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase()
                    + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }



    public static void underlineTextView(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void removeUnderlineTextView(TextView textView) {
        textView.setPaintFlags(0);
    }

    public static String getDeviceName() {
        @SuppressLint("HardwareIds")
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        return deviceName;
    }

    public static void copyTextToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    }


    public static String roundOffValue(double input) {
        return String.valueOf((int) Math.round(input));
    }

    public static String roundOffValue(String input) {
        return String.valueOf((int) Math.round(Double.parseDouble(input)));
    }


    public static void checkIfDarkMode(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isDarkModed())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    public static boolean isDarkModed() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }
}
