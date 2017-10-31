package com.lyancafe.coffeeshop.printer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/10/31.
 */

public class PrintSetting {

    private static final String PRINTER = "printer";
    public static final int WINPOS = 111;
    public static final int FUJITSU = 222;
    private static final String KEY_SMALL = "small";
    private static final String KEY_BIG = "big";

    public static void saveSmallPrinter(Context context,int printer){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_SMALL,printer).apply();
    }

    public static int getSmallPrinter(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_SMALL,WINPOS);
    }

    public static void saveBigPrinter(Context context,int printer){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_BIG,printer).apply();
    }

    public static int getBigPrinter(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_BIG,FUJITSU);
    }
}
