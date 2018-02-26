package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderStatus;
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
    private static final long PERIOD_AUTOPRODUCE = 30*1000;
    private long count = 0;

    //Test
    private Timer remindTimer;

    private Timer autoProduceTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCrate");
        startTimer();
//        startRemindTimer();
        startAutoProduceTimer();
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
                //查询所有订单
                List<OrderBean> list = OrderUtils.with().queryAllOrders();
                for(OrderBean order:list){
                    LogUtil.i(TAG,"order ="+order.toString());
                }
            }
        },0,PERIOD_CHECK);
    }

    private void startAutoProduceTimer(){
        if(autoProduceTimer==null){
            autoProduceTimer = new Timer(true);
        }
        autoProduceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<OrderBean> producingOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.PRODUCING);
                int cupsAmount = OrderHelper.getTotalQutity(producingOrders);
                boolean isAutoProduce = producingOrders.size()<3 || cupsAmount<10 ;
                LogUtil.d(TAG,"当前生产中订单为："+producingOrders.size()+"单 ，杯量为:"+cupsAmount);
                if(isAutoProduce){
                    List<OrderBean> toProducedOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.UNPRODUCED);
                    LogUtil.d(TAG,"当前待生产订单为："+toProducedOrders.size());
                    for(OrderBean order:toProducedOrders){
                        LogUtil.d(TAG,order.toString());
                    }
                }

            }
        },PERIOD_AUTOPRODUCE,PERIOD_AUTOPRODUCE);
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

        if(autoProduceTimer!=null){
            autoProduceTimer.cancel();
        }
    }


}
