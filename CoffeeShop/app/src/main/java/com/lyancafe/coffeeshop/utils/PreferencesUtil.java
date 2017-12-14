package com.lyancafe.coffeeshop.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/12/14.
 */

public class PreferencesUtil {

    private static String LOGIN = "login";

    private static String LOGIN_LAST_SUCCESS = "last_login_success";

    public static void putLastLoginAccount(Context context,String account){
        SharedPreferences sp = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        sp.edit().putString(LOGIN_LAST_SUCCESS,account).apply();
    }

    public static String getLastLoginAccount(Context context){
        SharedPreferences sp = context.getSharedPreferences(LOGIN,Context.MODE_PRIVATE);
        return sp.getString(LOGIN_LAST_SUCCESS,"");
    }

}
