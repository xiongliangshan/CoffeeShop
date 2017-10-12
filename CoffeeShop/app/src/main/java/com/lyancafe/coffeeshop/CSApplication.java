package com.lyancafe.coffeeshop;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lyancafe.coffeeshop.bean.DaoMaster;
import com.lyancafe.coffeeshop.bean.DaoSession;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyFileNameGenerator;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

import org.greenrobot.greendao.database.Database;

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
    private ApplicationLike tinkerApplicationLike;
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public static String REG_ID = "";

    public CSApplication() {
        Log.d(TAG,"CSApplication");
    }
    private  DaoSession daoSession;

    private HttpProxyCacheServer proxy;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTinkerPatch();
        LogUtil.d(TAG, "onCreate() process = " + getProcessName(this, Process.myPid()));
        application = this;
        getDeviceScreenSize();

        CrashReport.initCrashReport(getApplicationContext(), "900027459", false);

        //初始化Jpush
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this.getApplicationContext());     		// 初始化 JPush
        LogUtil.d(LogUtil.TAG_JPUSH,"JPush 开始初始化");

        setUpDatabase();

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

    private void initTinkerPatch() {
        // 我们可以从这里获得Tinker加载过程的信息
        if (BuildConfig.TINKER_ENABLE) {
            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
            // 初始化TinkerPatch SDK
            TinkerPatch.init(tinkerApplicationLike)
                    .reflectPatchLibrary()
                    .setPatchRollbackOnScreenOff(true)
                    .setPatchRestartOnSrceenOff(true)
                    .setFetchPatchIntervalByHours(1);
            // 获取当前的补丁版本
            Log.d(TAG, "Current patch version is " + TinkerPatch.with().getPatchVersion());

            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
            // 不同的是，会通过handler的方式去轮询
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }

    private void setUpDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"orders-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }


    public  DaoSession getDaoSession() {
        return daoSession;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        CSApplication app = (CSApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(MyUtil.getVideoCacheDir(this))
                .maxCacheSize(1024*1024*1024)
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
    }

}
