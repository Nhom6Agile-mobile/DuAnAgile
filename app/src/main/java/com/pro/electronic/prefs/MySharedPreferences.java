package com.pro.electronic.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String ELECTRONICS_STORE_PRO_PREFERENCES = "ELECTRONICS_STORE_PRO_PREFERENCES";
    private final Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void putLongValue(String key, long n) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, n);
        editor.apply();
    }

    public long getLongValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

    public void putIntValue(String key, int n) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, n);
        editor.apply();
    }

    public int getIntValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public void putStringValue(String key, String s) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.apply();
    }

    public String getStringValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public String getStringValue(String key, String defaultValue) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getString(key, defaultValue);
    }

    public void putBooleanValue(String key, Boolean b) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, b);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public void putFloatValue(String key, float f) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, f);
        editor.apply();
    }

    public float getFloatValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ELECTRONICS_STORE_PRO_PREFERENCES, Context.MODE_PRIVATE);
        return pref.getFloat(key, 0.0f);
    }

}
