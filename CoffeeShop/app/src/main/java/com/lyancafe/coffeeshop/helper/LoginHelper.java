package com.lyancafe.coffeeshop.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2015/9/22.
 */
public class LoginHelper {

    public static final String TAG = "LoginHelper";
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

    public static void saveLoginBean(Context context,LoginBean loginBean){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putString("login", JSON.toJSONString(loginBean)).commit();
    }

    public static LoginBean getLoginBean(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        String strLogin = sp.getString("login", "");
        if(TextUtils.isEmpty(strLogin)){
            Log.e("xls","getUser,userJsonStr is Empty");
            return new LoginBean();
        }else{
            return JSON.parseObject(strLogin,LoginBean.class);
        }

    }


    //保存当前模式， 是否是顺风单模式
    public static void saveSfFlag(Context context ,boolean isSF){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putBoolean("is_sf",isSF).commit();
    }

    //获取当前模式
    public static boolean getSfFlag(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getBoolean("is_sf", false);
    }

    //记录当天第一次登陆的时间
    public static void saveCurrentDayFirstLoginTime(Context context,long loginTime){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putLong("login_time",loginTime).commit();
    }

    //获取保存的登陆时间
    public static long getLoginTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getLong("login_time",0L);
    }

    //保存当前限单设置
    public static void saveLimitLevel(Context context,int limitLevel){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        sp.edit().putInt("limit_level",limitLevel).commit();
    }

    //获取当前限单设置
    public static int getLimitLevel(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return sp.getInt("limit_level", 1);
    }

    //判断是否是当天第一次登陆
    public static boolean isCurrentDayFirstLogin(Context context){
        long currentTime = System.currentTimeMillis();
        long firstTime = getLoginTime(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time1 = sdf.format(new Date(firstTime));
        String time2 = sdf.format(new Date(currentTime));
        if(time1.equals(time2)){
            Log.d(TAG,"not current day first login");
            return false;
        }else{
            saveCurrentDayFirstLoginTime(context,currentTime);
            Log.d(TAG, "is first login");
            return true;
        }
    }



    /**
     * 判断是否为顺风单模式
     * @return
     */
    public static boolean isSFMode(){
       return getSfFlag(CSApplication.getInstance());
    }
}
