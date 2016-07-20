package com.xls.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/8/31.
 */
public class HttpUtils {

//    public static String BASE_URL = "http://api.lyancafe.com/shop/v3/";

    public static String BASE_URL = "http://mtest.lyancafe.com/shop/v3/";

//    public static String BASE_URL = "http://192.168.1.186:8080/shop/v3/";
    


    /**
     * 判断是否联网
     */
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }


    /**
     * 判断是否wifi在线
     */
    public static boolean isWifiLine(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    /**
     *同步弹出Toast
     */
    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     *异步安全弹出toast
     */
    public static void showToastAsync(final Context context,final String toast)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    public static void showToastAsync(final Context context, int resId) {
        showToastAsync(context, context.getResources().getString(resId));
    }
}
