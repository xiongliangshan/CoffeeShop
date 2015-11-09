package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/9.
 */
public class AutoFetchOrdersService extends Service {

    private static final String TAG ="AutoFetchOrdersService";
    private Timer timer;
    private TimerTask task;
    private MyBinder binder = new MyBinder();
    public static final String ACTION_REFRESH_ORDERS = "acton_refresh_order";
    public static  boolean auto_flag = false;
    private static final long PERIOD_TIME = 30*1000;
    private int n = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCrate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        stopTimer();
        n = 0;
        return super.onUnbind(intent);
    }

    public void startTimer(){
        timer  = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "请求服务器--" + (n++));
                sendBroadCastToGetOrders();
            }
        };
        timer.schedule(task, 1000, PERIOD_TIME);
        auto_flag = true;
        ToastUtil.showToast(AutoFetchOrdersService.this,"已开启自动刷单模式");
    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        auto_flag = false;
        ToastUtil.showToast(AutoFetchOrdersService.this,"已关闭自动刷单模式");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    private void sendBroadCastToGetOrders(){
        Intent order_intent = new Intent();
        order_intent.setAction(ACTION_REFRESH_ORDERS);
        sendBroadcast(order_intent);
    }
    public class MyBinder extends Binder{

        public AutoFetchOrdersService getService(){
            return AutoFetchOrdersService.this;
        }
    }
}
