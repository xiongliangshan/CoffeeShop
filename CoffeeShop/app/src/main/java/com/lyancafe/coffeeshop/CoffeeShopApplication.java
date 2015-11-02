package com.lyancafe.coffeeshop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.lyancafe.coffeeshop.service.LocationService;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/21.
 */
public class CoffeeShopApplication extends Application {

    private static final String TAG  ="CoffeeShopApplication";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + File.separator+"coffeeshop"+File.separator;
    public static final String LOG_DIR = BASE_DIR+"log"+File.separator;
    private static CoffeeShopApplication application;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public Set<String> printedSet;

    public CoffeeShopApplication() {
        Log.d(TAG,"CoffeeShopApplication");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        application = this;
        printedSet = new HashSet<String>();
        getDeviceScreenSize();
        Intent intent =  new Intent(CoffeeShopApplication.this, LocationService.class);
        this.startService(intent);
  /*      CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);*/
    }

    public static CoffeeShopApplication getInstance(){
        if(application==null){
            application = new CoffeeShopApplication();
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