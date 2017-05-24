package com.lyancafe.coffeeshop.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;


/**
 * Created by Administrator on 2015/9/22.
 */
public class LoginHelper {

    public static final String TAG = "LoginHelper";
    public static int LOGIN_SUCCESS = 0;   //登录成功
    public static int LOGIN_FAIL = 104;    //登录失败
    private static String PREFERENCES_USER = "user";

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

    public static void saveUser(Context context, UserBean userBean){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        String userJson = new Gson().toJson(userBean);
        LogUtil.d("login","存user :"+userJson);
        sp.edit().putString("login", userJson).apply();
    }

    public static UserBean getUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        String strLogin = sp.getString("login", "");
        LogUtil.d("login","取user :"+strLogin);
        if(TextUtils.isEmpty(strLogin)){
            Log.e("xls","getUser,userJsonStr is Empty");
            return new UserBean();
        }else{
            return new Gson().fromJson(strLogin,UserBean.class);
        }

    }



    //记录当天第一次登陆的时间
    public static void saveCurrentDayFirstLoginTime(Context context,long loginTime){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putLong("login_time",loginTime).apply();
    }

    //获取保存的登陆时间
    public static long getLoginTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getLong("login_time",0L);
    }


}
