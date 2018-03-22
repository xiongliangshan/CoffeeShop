package com.lyancafe.coffeeshop.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangjz 2018/3/21.
 */

public class ProductHelper {

    private static String PREFERENCES_USER = "product";

    public static void saveProduct(Context context, JsonObject jsonProduct){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        String userJson = new Gson().toJson(jsonProduct);
        sp.edit().putString("capacity", userJson).apply();
    }

    public static Map<String,Object> getProduct(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        String strLogin = sp.getString("capacity", "");
        if(TextUtils.isEmpty(strLogin)){
            return new HashMap<>();
        } else {
            return new Gson().fromJson(strLogin, Map.class);
        }
    }

}
