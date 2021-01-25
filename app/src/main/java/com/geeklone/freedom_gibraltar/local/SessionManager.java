package com.geeklone.freedom_gibraltar.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * developed by Irfan A.
 */

public class SessionManager {
    final String APP_STATUS = "APP_STATUS";
    final String EXP_DATE = "EXP_DATE";
    final String LOGIN_SATE = "LOGIN_SATE";
    final String AUTH_TOKEN = "AUTH_TOKEN";
    final String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
    final String U_ID = "U_ID";
    final String USER_NAME = "USER_NAME";
    final String USER_PHONE = "USER_PHONE";
    final String IS_ADMIN_LOGIN = "IS_ADMIN_LOGIN";
    final String AVB_BALANCE = "AVB_BALANCE";
    final String DARK_MODE = "DARK_MODE";
    final String TOKEN = "TOKEN";
    final String DEVICE_TOKEN = "DEVICE_TOKEN";
//    final String DARK_MODE = "DARK_MODE";
//    final String DARK_MODE = "DARK_MODE";


    private Context context;

    public SessionManager(Context context) {
        this.context = context;
    }

    private SharedPreferences.Editor getPreferencesEditor() {
        return getSharedPreferences().edit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("mAppPref", Context.MODE_PRIVATE);
    }

    public void setIsFirstTime(boolean flag) {
        getPreferencesEditor().putBoolean(APP_STATUS, flag).commit();
    }

    public boolean getIsFirstTime() {
        return getSharedPreferences().getBoolean(APP_STATUS, true);
    }

    public void saveExpDate(String string) {
        getPreferencesEditor().putString(EXP_DATE, string).commit();
    }

    public String getExpDate() {
        return getSharedPreferences().getString(EXP_DATE, null);
    }

    public void setLocale(String s) {
        getPreferencesEditor().putString(SELECTED_LANGUAGE, s).commit();
    }

    public String getLocale() {
        return getSharedPreferences().getString(SELECTED_LANGUAGE, "en");
    }


    public void setIsLoggedIn(boolean flag) {
        getPreferencesEditor().putBoolean(LOGIN_SATE, flag).commit();
    }

    public boolean getIsLoggedIn() {
        return getSharedPreferences().getBoolean(LOGIN_SATE, false);
    }


    public void setUserName(String value) {
        getPreferencesEditor().putString(USER_NAME, value).commit();
    }

    public String getUserName() {
        return getSharedPreferences().getString(USER_NAME, null);
    }

    public void setUserPhone(String value) {
        getPreferencesEditor().putString(USER_PHONE, value).commit();
    }

    public String getUserPhone() {
        return getSharedPreferences().getString(USER_PHONE, null);
    }

    public void setUserID(String value) {
        getPreferencesEditor().putString(U_ID, value).apply();
    }

    public String getUserID() {
        return getSharedPreferences().getString(U_ID, null);
    }

    public void setAvailableBalance(float value) {
        getPreferencesEditor().putFloat(AVB_BALANCE, value).commit();
    }

    public float getAvailableBalance() {
        return getSharedPreferences().getFloat(AVB_BALANCE, 0);
    }


    public void setDeviceToken(String value) {
        getPreferencesEditor().putString(DEVICE_TOKEN, value).commit();
    }

    public String getDeviceToken() {
        return getSharedPreferences().getString(DEVICE_TOKEN, null);
    }

    public void saveDarkModeState(boolean flag) {
        getPreferencesEditor().putBoolean(DARK_MODE, flag).commit();
    }

    public boolean isDarkModed() {
        return getSharedPreferences().getBoolean(DARK_MODE, false);
    }

    public void setAuthToken(String value) {
        getPreferencesEditor().putString(TOKEN, value).commit();
    }

    public String getAuthToken() {
        return getSharedPreferences().getString(TOKEN, null);
    }


}
