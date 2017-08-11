package com.lyancafe.coffeeshop.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.PushMessageBean;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.tinkerpatch.sdk.TinkerPatch;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/10/26.
 */
public class MyPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPushReceiver";
    NotificationManager mNotificationManager;
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            LogUtil.d("jpush","JPush 初始化成功,registrationId = "+regId);
            CSApplication.REG_ID = regId;

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + message);
            PushMessageBean pmb = PushMessageBean.convertToBean(message);
            if(pmb!=null){
                sendNotification(context,pmb);
            }else{
                if("hotfix".equalsIgnoreCase(message)){
                    TinkerPatch.with().fetchPatchUpdate(true);
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    /**
     * 初始化通知栏消息
     */
    private void sendNotification(final Context context,PushMessageBean pmb){
        if(mNotificationManager==null){
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentTitle("连咖啡消息通知");
        if(pmb!=null){
            mBuilder.setContentText(pmb.getContent());
        }else{
            mBuilder.setContentText("有订单消息，数据解析错误无法显示单号");
        }
        if(pmb.getEventType()==1){  //新订单消息
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.coffee_box));
            EventBus.getDefault().post(new NewOderComingEvent(0L));
        }else if(pmb.getEventType()==10){   //小哥上报问题
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        }else if(pmb.getEventType()==11){   //问题已经解决
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        }else if(pmb.getEventType()==16){   //订单撤回
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.order_undo));
        }else if(pmb.getEventType()==20){   //订单催单
            mBuilder.setContentTitle(pmb.getTitle());
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.urge_shop));
        }


        Random ran =new Random(System.currentTimeMillis());
        final int notifyId  = ran.nextInt();
        Log.d(TAG,"notifyId = "+notifyId);
        mNotificationManager.notify(notifyId, mBuilder.build());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                mNotificationManager.cancel(notifyId);
            }
        }, 2 * 60 * 1000);
    }


}
