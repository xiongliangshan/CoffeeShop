package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.SoundPoolUtil;

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
    private AutoProduceTask autoProduceTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCrate");
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        if(user.isOpenFulfill()){
            startAutoProduceTimer();
        }else {
            closeAutoProduceTimer();
        }
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


    private void startAutoProduceTimer(){
        if(autoProduceTimer==null){
            autoProduceTimer = new Timer(true);
        }
        if(autoProduceTask == null){
            autoProduceTask = new AutoProduceTask();
        }else {
            autoProduceTask.cancel();
            autoProduceTask = new AutoProduceTask();
        }
        LogUtil.d(TAG,"自动生产服务启动");
        autoProduceTimer.schedule(autoProduceTask,PERIOD_AUTOPRODUCE,PERIOD_AUTOPRODUCE);
    }

    private void closeAutoProduceTimer(){
        LogUtil.d(TAG,"自动生产服务关闭");
        if(autoProduceTimer!=null){
            autoProduceTimer.cancel();
            autoProduceTimer = null;
        }
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

        closeAutoProduceTimer();
    }


    class AutoProduceTask extends TimerTask{

        @Override
        public void run() {
            List<OrderBean> producingOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.PRODUCING);
            int cupsAmount = OrderHelper.getTotalQutity(producingOrders);
            boolean isAutoProduce = producingOrders.size()<3 || cupsAmount<10 ;
            LogUtil.d(TAG,"当前生产中订单为："+producingOrders.size()+"单 ，杯量为:"+cupsAmount);
            Logger.getLogger().log("当前生产中订单为:"+producingOrders.size()+"单,杯量为:"+cupsAmount+"}");
            if(isAutoProduce){
                List<OrderBean> toProducedOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.UNPRODUCED);
                LogUtil.d(TAG,"当前待生产订单为："+toProducedOrders.size());
                int n = 0;
                for(OrderBean orderBean:toProducedOrders){
                    if(orderBean.getPriority()==0){
                        long nowTime = System.currentTimeMillis();
                        if(nowTime>=orderBean.getStartProduceTime()){
                            //开始自动生产
                            LogUtil.d(TAG,"满足条件，开始自动生产:"+orderBean.getId());
                            Logger.getLogger().log("自动生产订单:{"+orderBean.getId()+"priority = "+orderBean.getPriority()+"}");
                            EventBus.getDefault().postSticky(new StartProduceEvent(orderBean));
                            n++;
                        }
                    }else {
                        LogUtil.d(TAG,"特殊订单，开始自动生产:"+orderBean.getId());
                        Logger.getLogger().log("自动生产订单:{"+orderBean.getId()+"priority = "+orderBean.getPriority()+"}");
                        EventBus.getDefault().postSticky(new StartProduceEvent(orderBean));
                        n++;
                    }

                }
                if(n>0){
                    //开始生产订单语音播放
                    SoundPoolUtil.create(CSApplication.getInstance(), R.raw.start_produce);
                }else {
                    LogUtil.d(TAG,"时间未到，没有满足条件的订单");
                }
            }else {
                LogUtil.d(TAG,"任务堆积，暂缓生产");
            }
        }
    }


}
