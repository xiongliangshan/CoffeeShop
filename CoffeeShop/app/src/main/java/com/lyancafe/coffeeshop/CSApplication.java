package com.lyancafe.coffeeshop;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/8/21.
 */
public class CSApplication extends Application {

    private static final String TAG  ="CSApplication";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + File.separator+"coffeeshop"+File.separator;
    public static final String APK_DIR = BASE_DIR+"apk";
    public static final String CACHE_DIR = BASE_DIR+"cache";
    public static final String LOG_DIR = BASE_DIR+"log"+File.separator;
    private static CSApplication application;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public static String REG_ID = "";

    public CSApplication() {
        Log.d(TAG,"CSApplication");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate() process = " + getProcessName(this, Process.myPid()));
        application = this;
        getDeviceScreenSize();

        CrashReport.initCrashReport(getApplicationContext(), "900027459", false);

        //初始化Jpush
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this.getApplicationContext());     		// 初始化 JPush
        LogUtil.d(LogUtil.TAG_JPUSH,"JPush 开始初始化");

        //初始化百度
        SDKInitializer.initialize(getApplicationContext());

    }

    public static CSApplication getInstance(){
        if(application==null){
            application = new CSApplication();
        }
        return application;
    }


    private void getDeviceScreenSize(){
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate()");
    }


    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }


}
