package com.lyancafe.coffeeshop.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/10/26.
 */
public class ShopHelper {

    public static String PREFERENCES_USER = "shop";


    public static void saveShopStatus(Context context,String status){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putString("shop_id", status).commit();
    }
    public static String getShopStatus(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getString("shop_id","G");
    }
}
