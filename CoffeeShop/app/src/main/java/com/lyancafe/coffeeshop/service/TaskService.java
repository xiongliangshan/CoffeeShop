package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
import com.lyancafe.coffeeshop.event.ShowUnfinishedEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TaskService extends Service {

    private static final String TAG ="TaskService";
    private static final int NOTIFICATION_ID = 111;
    private Timer timer;
    private static final long PERIOD_TIME = 2*60*1000;
    private static final long PERIOD_AUTOPRODUCE = 20*1000;

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
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder.setAutoCancel(false);
        builder.setShowWhen(false);
        builder.setSmallIcon(R.mipmap.app_icon);
        builder.setContentTitle("咖啡屋App正在运行");
        builder.setContentText("应用服务");
        startForeground(NOTIFICATION_ID,builder.build());
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
        stopForeground(true);
    }


    class AutoProduceTask extends TimerTask {

        @Override
        public void run() {
            final int cupTotal = 6;
            List<OrderBean> toProducedOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.UNPRODUCED);
//            LogUtil.d(TAG, "当前待生产订单为：" + toProducedOrders);
            for (OrderBean orderBean : toProducedOrders) {
                List<OrderBean> producingOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.PRODUCING);
//                LogUtil.d(TAG, "当前生产中订单为：" + producingOrders);
                int cupsAmount = OrderHelper.getTotalQutity(producingOrders);
                boolean isAutoProduce = producingOrders.size() < 3 || cupsAmount < cupTotal;
                LogUtil.d(TAG, "当前生产中订单为：" + producingOrders.size() + "单 ，杯量为:" + cupsAmount);
                Logger.getLogger().log("当前生产中订单为:" + producingOrders.size() + "单,杯量为:" + cupsAmount + "}");
                if (isAutoProduce) {
                    EventBus.getDefault().postSticky(new ShowUnfinishedEvent("未完成订单过多，无法自动打印，请及时完成！",false));
                    if (orderBean.getPriority() == 0) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime >= orderBean.getStartProduceTime()) {
                            //开始自动生产
                            LogUtil.d(TAG, "满足条件，开始自动生产:" + orderBean.getId());
                            EventBus.getDefault().postSticky(new StartProduceEvent(orderBean,true));
                        }else {
                            Logger.getLogger().log("订单生产时间未到:"+orderBean.getId());
                        }
                    } else {
                        LogUtil.d(TAG, "特殊订单，开始自动生产:" + orderBean.getId());
                        EventBus.getDefault().postSticky(new StartProduceEvent(orderBean,true));
                    }
                } else {
                    EventBus.getDefault().postSticky(new ShowUnfinishedEvent("未完成订单过多，无法自动打印，请及时完成！",true));
                    LogUtil.d(TAG, "任务堆积，暂缓生产");
                    Logger.getLogger().log("任务堆积，暂缓生产");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Logger.getLogger().log("sleep 抛出异常");
                }

            }

        }
    }


}
