package com.practice.easyexam.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Constants;

public class SharedPref {

    private static SharedPref instance = null;

    private static final String sharedPreferencesName = "kevinSharedPref";

    private SharedPref() {
    }

    public static SharedPref getInstance() {
        if (instance == null) {
            instance = new SharedPref();
        }
        return instance;
    }

    public void setUser(Context context, User user){
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.USER,new Gson().toJson(user));
        editor.apply();
    }



    public User getUser(Context context){
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return new Gson().fromJson(pref.getString(Constants.USER,""), User.class);
    }

    public void clearSharedPref(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }


    public void setRefreshToken(Context context, String  token){
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.REFRESH_TOKEN,token);
        editor.apply();
    }



    public String getRefreshToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return pref.getString(Constants.REFRESH_TOKEN,"");
    }

    public void clearRefreshToken(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }



}
