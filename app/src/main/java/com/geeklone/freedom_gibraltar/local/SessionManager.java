package com.geeklone.freedom_gibraltar.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * developed by Irfan A.
 */

public class SessionManager {
    final String APP_STATUS = "APP_STATUS";
    final String EXP_DATE = "EXP_DATE";
    final String PWD = "PWD";
    final String USER_EMAIL = "USER_PHONE";
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
    final String IMG = "IMG";


    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
    }

    private SharedPreferences.Editor getPreferencesEditor() {
        return getSharedPreferences().edit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("mAppPref", Context.MODE_PRIVATE);
    }

    public boolean getIsFirstTime() {
        return getSharedPreferences().getBoolean(APP_STATUS, true);
    }

    public void setIsFirstTime(boolean flag) {
        getPreferencesEditor().putBoolean(APP_STATUS, flag).commit();
    }

    public void saveExpDate(String string) {
        getPreferencesEditor().putString(EXP_DATE, string).commit();
    }

    public String getExpDate() {
        return getSharedPreferences().getString(EXP_DATE, null);
    }

    public String getLocale() {
        return getSharedPreferences().getString(SELECTED_LANGUAGE, "en");
    }

    public void setLocale(String s) {
        getPreferencesEditor().putString(SELECTED_LANGUAGE, s).commit();
    }

    public boolean getIsLoggedIn() {
        return getSharedPreferences().getBoolean(LOGIN_SATE, false);
    }

    public void setIsLoggedIn(boolean flag) {
        getPreferencesEditor().putBoolean(LOGIN_SATE, flag).commit();
    }

    public String getUserName() {
        return getSharedPreferences().getString(USER_NAME, null);
    }

    public void setUserName(String value) {
        getPreferencesEditor().putString(USER_NAME, value).commit();
    }

    public String getUserMobile() {
        return getSharedPreferences().getString(USER_PHONE, null);
    }

    public void setUserMobile(String value) {
        getPreferencesEditor().putString(USER_PHONE, value).commit();
    }

//    public String getUserID() {
//        return getSharedPreferences().getString(U_ID, null);
//    }
//
//    public void setUserID(String value) {
//        getPreferencesEditor().putString(U_ID, value).apply();
//    }

    public float getAvailableBalance() {
        return getSharedPreferences().getFloat(AVB_BALANCE, 0);
    }

    public void setAvailableBalance(float value) {
        getPreferencesEditor().putFloat(AVB_BALANCE, value).commit();
    }

    public String getDeviceToken() {
        return getSharedPreferences().getString(DEVICE_TOKEN, null);
    }

    public void setDeviceToken(String value) {
        getPreferencesEditor().putString(DEVICE_TOKEN, value).commit();
    }

    public void saveDarkModeState(boolean flag) {
        getPreferencesEditor().putBoolean(DARK_MODE, flag).commit();
    }

    public boolean isDarkModed() {
        return getSharedPreferences().getBoolean(DARK_MODE, false);
    }

    public String getAuthToken() {
        return getSharedPreferences().getString(TOKEN, null);
    }

    public void setAuthToken(String value) {
        getPreferencesEditor().putString(TOKEN, value).commit();
    }

    public boolean getIsAdmin() {
        return getSharedPreferences().getBoolean(IS_ADMIN_LOGIN, true);
    }

    public void setIsAdmin(boolean flag) {
        getPreferencesEditor().putBoolean(IS_ADMIN_LOGIN, flag).commit();
    }

    public String getUid() {
        return getSharedPreferences().getString(U_ID, null);
    }

    public void setUid(String value) {
        getPreferencesEditor().putString(U_ID, value).commit();
    }

    public String getUsername() {
        return getSharedPreferences().getString(USER_NAME, "");
    }

    public void setUsername(String value) {
        getPreferencesEditor().putString(USER_NAME, value).commit();
    }

    public String getUserEmail() {
        return getSharedPreferences().getString(USER_EMAIL, "");
    }

    public void setUserEmail(String value) {
        getPreferencesEditor().putString(USER_EMAIL, value).commit();
    }

    public String getUserPwd() {
        return getSharedPreferences().getString(PWD, "");
    }

    public void setUserPwd(String string) {
        getPreferencesEditor().putString(PWD, string).commit();
    }

    public String getUserImg() {
        return getSharedPreferences().getString(IMG, "");
    }

    public void setUserImg(String s) {
        getPreferencesEditor().putString(IMG, s).commit();
    }


}
