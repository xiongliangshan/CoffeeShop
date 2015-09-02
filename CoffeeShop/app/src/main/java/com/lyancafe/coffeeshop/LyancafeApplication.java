package com.lyancafe.coffeeshop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.lyancafe.coffeeshop.service.LocationService;
import com.lyancafe.coffeeshop.utils.CrashHandler;

import java.io.File;

/**
 * Created by Administrator on 2015/8/21.
 */
public class LyancafeApplication extends Application {

    private static final String TAG  ="LyancafeApplication";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + File.separator+"coffeeshop"+File.separator;
    public static final String LOG_DIR = BASE_DIR+"log"+File.separator;
    private static LyancafeApplication application;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public LyancafeApplication() {
        Log.d(TAG,"LyancafeApplication");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        application = this;
        getDeviceScreenSize();
        Intent intent =  new Intent(LyancafeApplication.this, LocationService.class);
        this.startService(intent);
  /*      CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);*/
    }

    public static LyancafeApplication getInstance(){
        if(application==null){
            application = new LyancafeApplication();
        }
        return application;
    }


    private void getDeviceScreenSize(){
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        Log.d(TAG,"screenWidth = "+screenWidth+"  , screenHeight = "+screenHeight);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate()");
    }
}
