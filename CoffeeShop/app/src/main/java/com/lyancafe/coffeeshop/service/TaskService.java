package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TaskService extends Service {

    private static final String TAG ="TaskService";
    private Timer timer;
    private static final long PERIOD_TIME = 2*60*1000;
    private static final long PERIOD_CHECK = 3*60*1000;
    private long count = 0;

    //Test
    private Timer remindTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCrate");
        startTimer();
        startRemindTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startTimer(){
        LogUtil.d(TAG, "启动 timer");
        if(timer==null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.d(TAG, "发送新订单刷新消息--");
                EventBus.getDefault().postSticky(new NewOderComingEvent(0L));
            }
        }, PERIOD_TIME, PERIOD_TIME);
    }

    private void startRemindTimer() {
        if(remindTimer==null){
            remindTimer = new Timer(true);
        }
        remindTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.i(TAG,"myTimer is running count = "+count++);
                //检查订单超时未取的情况
                //test
                List<OrderBean> list = OrderUtils.with().queryAllOrders();
                for(OrderBean order:list){
                    LogUtil.i(TAG,"order ="+order);
                }
            }
        },0,PERIOD_CHECK);
    }

    private void checkOrdersForUnFetch(){

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(timer!=null){
            timer.cancel();
        }
        if(remindTimer!=null){
            remindTimer.cancel();
        }
    }


}
