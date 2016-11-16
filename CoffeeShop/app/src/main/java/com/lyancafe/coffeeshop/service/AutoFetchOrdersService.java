package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lyancafe.coffeeshop.event.CommentCountEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;

import org.greenrobot.eventbus.EventBus;

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
        startCommentTimer();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        if(auto_flag){
            stopTimer();
        }
        n = 0;

        stopCommentTimer();
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
        timer.schedule(task, 1000, PERIOD_TIME);
        auto_flag = true;
    //    ToastUtil.showToast(AutoFetchOrdersService.this,"已开启自动刷单模式");
    //    PushManager.getInstance().turnOffPush(AutoFetchOrdersService.this);
    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        auto_flag = false;
    //    ToastUtil.showToast(AutoFetchOrdersService.this,"已关闭自动刷单模式");
    //    PushManager.getInstance().turnOnPush(AutoFetchOrdersService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    public class MyBinder extends Binder{

        public AutoFetchOrdersService getService(){
            return AutoFetchOrdersService.this;
        }
    }




    //刷新评论有关
    public void startCommentTimer(){
        commentTimer  = new Timer();
        commentTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "请求服务器--" + (n++));
                EventBus.getDefault().post(new CommentCountEvent());
            }
        };
        commentTimer.schedule(commentTask, 1000, COMMENT_PERIOD_TIME);
        Log.d("xiongliangshan", "startCommentTimer ----");
    }

    public void stopCommentTimer(){
        Log.d("xiongliangshan","---- stopCommentTimer");
        if(commentTimer!=null){
            commentTimer.cancel();
            commentTimer=null;
        }
    }


}
