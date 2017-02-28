package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lyancafe.coffeeshop.event.NewOderComingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TaskService extends Service {

    private static final String TAG ="TaskService";
    private Timer timer;
    private TimerTask task;
    private MyBinder binder = new MyBinder();
    public static  boolean auto_flag = false;
    private static final long PERIOD_TIME = 2*60*1000;
    private int n = 0;

    //评论数量刷新定时器
    private Timer commentTimer;
    private TimerTask commentTask;
    private static final long COMMENT_PERIOD_TIME = 2*60*1000;//刷新评论间隔时间

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCrate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        startTimer();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        if(auto_flag){
            stopTimer();
        }
        n = 0;
        return super.onUnbind(intent);
    }

    public void startTimer(){
        timer  = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "请求服务器--" + (n++));
                EventBus.getDefault().post(new NewOderComingEvent(0L));
            }
        };
        timer.schedule(task, PERIOD_TIME, PERIOD_TIME);
        auto_flag = true;
    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        auto_flag = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    public class MyBinder extends Binder{

        public TaskService getService(){
            return TaskService.this;
        }
    }

}
