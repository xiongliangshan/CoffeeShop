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
    private static final String KEY_SIMPLIFY_SWITCH = "simplify_switch";
    private static final String KEY_MATERIAL = "material";
    private static final String PRINT_TIME = "print_time";
    private static final String PRINT_SECOND = "print_second";

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
        return sharedPreferences.getInt(KEY_BIG,WINPOS);
    }

    public static void saveSimplifyEnable(Context context,boolean isOpen){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_SIMPLIFY_SWITCH,isOpen).apply();
    }

    public static boolean isSimplifyEnable(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_SIMPLIFY_SWITCH,false);
    }

    public static void savePrintTime(Context context, boolean isOpen) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PRINT_TIME, isOpen).apply();
    }

    public static boolean isPrintTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PRINT_TIME, false);
    }

    public static boolean isPrintSecond(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PRINT_SECOND, false);
    }

    public static void savePrintSecond(Context context, boolean isOpen) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRINTER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PRINT_SECOND, isOpen).apply();
    }

}
