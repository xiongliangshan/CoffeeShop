package com.lyancafe.coffeeshop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.ShopHelper;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.tencent.bugly.crashreport.CrashReport;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/21.
 */
public class CoffeeShopApplication extends Application {

    private static final String TAG  ="CoffeeShopApplication";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + File.separator+"coffeeshop"+File.separator;
    public static final String LOG_DIR = BASE_DIR+"log"+File.separator;
    private static CoffeeShopApplication application;

    public static LatLng latLng = null;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

//    public Set<String> printedSet;

    public CoffeeShopApplication() {
        Log.d(TAG,"CoffeeShopApplication");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        application = this;
        SDKInitializer.initialize(this);
        getDeviceScreenSize();
        //进行定位
        ShopLocation shopLocation = new ShopLocation(this);
        shopLocation.startLocation();

        CrashReport.initCrashReport(getApplicationContext(), "900027459", false);
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


    //退出登录接口
    public static class LoginOutQry implements Qry{

        private Context context;

        public LoginOutQry(Context context) {
            this.context = context;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+"/token/delete?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.GET, url, params), context, this, false);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "LoginOutQry:" + resp);
            Intent intent_update = new Intent(context, UpdateService.class);
            context.stopService(intent_update);
        }
    }
}
