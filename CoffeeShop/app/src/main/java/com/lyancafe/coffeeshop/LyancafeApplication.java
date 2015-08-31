package com.lyancafe.coffeeshop;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;

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

    public LyancafeApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Intent intent =  new Intent(LyancafeApplication.this, LocationService.class);
        this.startService(intent);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static LyancafeApplication getInstance(){
        if(application==null){
            application = new LyancafeApplication();
        }
        return application;
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
