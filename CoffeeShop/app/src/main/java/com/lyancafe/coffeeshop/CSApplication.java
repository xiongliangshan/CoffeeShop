package com.lyancafe.coffeeshop;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.tencent.bugly.crashreport.CrashReport;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/8/21.
 */
public class CSApplication extends Application {

    private static final String TAG  ="CSApplication";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + File.separator+"coffeeshop"+File.separator;
    public static final String APK_DIR = BASE_DIR+"apk";
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
        Log.d(TAG, "onCreate()");
        application = this;
        getDeviceScreenSize();

        CrashReport.initCrashReport(getApplicationContext(), "900027459", false);

        //初始化Jpush
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        //初始化okGo
        OkGo.init(this);
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)
                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(20*1000)  //全局的连接超时时间
                    .setReadTimeOut(20*1000)     //全局的读取超时时间
                    .setWriteTimeOut(20*1000)    //全局的写入超时时间
                    // 可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)
                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(1)
                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效
                    // 可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                                  //方法一：信任所有证书,不安全有风险
                    //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            LoginBean loginBean = LoginHelper.getLoginBean(context);
            String token = loginBean.getToken();
            String url = Urls.BASE_URL+"/token/delete?token="+token;
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

    public static class CheckUpdateQry implements Qry {

        private Context context;
        private int curVersion;
        private boolean isShowToast;

        public CheckUpdateQry(Context context, int curVersion, boolean isShowToast) {
            this.context = context;
            this.curVersion = curVersion;
            this.isShowToast = isShowToast;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getLoginBean(context).getToken();
            String url = Urls.BASE_URL + "/token/"+curVersion+"/isUpdateApp?token="+token;
            Map<String,Object> params = new HashMap<>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, isShowToast);
        }

        @Override
        public void showResult(Jresp resp) {
            if(resp==null){
                Log.e(TAG, "resp = " + resp);
                return;
            }
            Log.d(TAG, "resp = " + resp);
            if(resp.status==0){
                final ApkInfoBean apkInfoBean = ApkInfoBean.parseJsonToBean(resp.data.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.confirm_download, apkInfoBean.getAppName()));
                builder.setTitle(context.getResources().getString(R.string.version_update));
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //启动Service下载apk文件
                        Intent intent = new Intent(context, UpdateService.class);
                        intent.putExtra("apk",apkInfoBean);
                        context.startService(intent);
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.cacel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }else {
                if(isShowToast){
                    ToastUtil.show(context, resp.message);
                }
            }

        }
    }
}