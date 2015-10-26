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
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.PushMessageBean;

import java.util.Random;

/**
 * Created by Administrator on 2015/10/26.
 */
public class GetuiPushReceiver extends BroadcastReceiver {

    private static final String TAG = "getui";
    public static String CID = "";
    NotificationManager mNotificationManager;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    Log.d(TAG, "receiver payload : " + data);
                    sendNotification(context, null);
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                if(!TextUtils.isEmpty(cid)){
            //        LoginHelper.sendClientIdToServer(context,cid);
                    GetuiPushReceiver.CID = cid;
                }
                Log.d(TAG,"cid = "+cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            case PushConsts.GET_SDKONLINESTATE:
                /**
                 * 检测设备是否在线
                 */
                boolean isOnline = bundle.getBoolean("onlineState");
                Log.d(TAG,"isOnline = "+isOnline);

                break;
            default:
                break;
        }
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
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentTitle("透传")
                .setContentText("测试消息");

        mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.coffee_box));

        Random ran =new Random(System.currentTimeMillis());
        final int notifyId  = ran.nextInt();
        Log.d(TAG,"notifyId = "+notifyId);
        mNotificationManager.notify(notifyId, mBuilder.build());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mNotificationManager==null){
                    mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                mNotificationManager.cancel(notifyId);
            }
        },2*60*1000);
    }
}
