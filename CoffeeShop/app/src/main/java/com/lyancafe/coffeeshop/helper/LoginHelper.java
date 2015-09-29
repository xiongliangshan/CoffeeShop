package com.lyancafe.coffeeshop.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;


/**
 * Created by Administrator on 2015/9/22.
 */
public class LoginHelper {

    public static int LOGIN_SUCCESS = 0;   //登录成功
    public static int LOGIN_FAIL = 104;    //登录失败
    public static String PREFERENCES_USER = "user";

    public static boolean verifyLoginParams(Context context,String userName,String password){
        if(TextUtils.isEmpty(userName)){
            ToastUtil.showToast(context, R.string.username_empty_prompt);
            return false;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showToast(context,R.string.password_empty_prompt);
            return false;
        }

        return true;
    }

    public static void saveToken(Context context,String token){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putString("token",token).commit();
    }

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getString("token","");
    }

    public static void saveUserId(Context context,int userId){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putInt("user_id", userId).commit();
    }
    public static int getUserId(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getInt("user_id",0);
    }

    public static void saveShopId(Context context,int shopId){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putInt("shop_id", shopId).commit();
    }
    public static int getShopId(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getInt("shop_id",0);
    }

    public static void  saveUserInfo(Context context,int userId,int shopId,String token){
        saveUserId(context,userId);
        saveShopId(context,shopId);
        saveToken(context,token);
    }
}
