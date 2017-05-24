package com.lyancafe.coffeeshop.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/18.
 */

public class LogUtil {

    private static boolean debug = true;
    public final static String TAG_LOGIN = "login";
    public final static String TAG_JPUSH = "jpush";
    public final static String TAG_TIMER = "timer";
    public final static String TAG_PRODUCE = "produce";
    public final static String TAG_SHOP = "shop";

    public static void v(String tag,String message){
        if(debug){
            Log.v(tag,message);
        }
    }
    public static void d(String tag,String message){
        if(debug){
            Log.d(tag,message);
        }
    }
    public static void i(String tag,String message){
        if(debug){
            Log.i(tag,message);
        }
    }

    public static void w(String tag,String message){
        if(debug){
            Log.w(tag,message);
        }
    }
    public static void e(String tag,String message){
        if(debug){
            Log.e(tag,message);
        }
    }


}
