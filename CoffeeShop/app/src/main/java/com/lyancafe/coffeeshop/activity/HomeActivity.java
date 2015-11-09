package com.lyancafe.coffeeshop.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.igexin.sdk.PushManager;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.fragment.ShopManagerFragment;
import com.lyancafe.coffeeshop.service.AutoFetchOrdersService;
import com.lyancafe.coffeeshop.widget.SettingWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeActivity extends BaseActivity {

    private static final String TAG ="HomeActivity";
    public static int currIndex = 0;
    private static RadioGroup mRadioGroup;
    public List<Fragment> fragments_list = new ArrayList<Fragment>();
    private OrdersFragment orderFrag;
    private OrderQueryFragment orderQueryFrag;
    private ShopManagerFragment shopManagerFrag;
    private FragmentTabAdapter tabAdapter;
    private ImageButton settingBtn;
    private ImageView baristaLogoIV;
    private SettingWindow sw;
    private AutoFetchOrdersService autoFetchOrdersService;
    NotificationManager mNotificationManager;
    private ServiceConnection serviceConnection;

    private Context context;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context  = HomeActivity.this;
        //初始化个推
        PushManager.getInstance().initialize(this.getApplicationContext());

        setContentView(R.layout.activity_home);
        initViews();
        initFragments();
        //启动service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("AutoFetchOrdersService","onServiceConnected");
                AutoFetchOrdersService.MyBinder myBinder = (AutoFetchOrdersService.MyBinder)service;
                autoFetchOrdersService = myBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("AutoFetchOrdersService","onServiceDisconnected");
            }
        };
        Intent intent=new Intent(HomeActivity.this,AutoFetchOrdersService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("AutoFetchOrdersService", "bindService");
    }

    private void initViews(){
        baristaLogoIV = (ImageView) findViewById(R.id.iv_barista_logo);
        baristaLogoIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("AutoFetchOrdersService", "onLongClick");
                if (autoFetchOrdersService != null) {
                    if (!autoFetchOrdersService.auto_flag) {
                        autoFetchOrdersService.startTimer();
                        sendNotificationForAutoOrders(true);
                    } else {
                        autoFetchOrdersService.stopTimer();
                        sendNotificationForAutoOrders(false);
                    }
                }
                return false;
            }
        });
        mRadioGroup = (RadioGroup) findViewById(R.id.group_left);
        settingBtn = (ImageButton) findViewById(R.id.btn_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //左下角的设置
                if (sw != null) {
                    sw.showSettingWindow(v);
                } else {
                    SettingWindow sw = new SettingWindow(HomeActivity.this);
                    sw.showSettingWindow(v);
                }

            }
        });
    }
    private void initFragments(){
        orderFrag =  new OrdersFragment();
        orderQueryFrag = new OrderQueryFragment();
        shopManagerFrag = new ShopManagerFragment();
        fragments_list.add(orderFrag);
        fragments_list.add(orderQueryFrag);
        fragments_list.add(shopManagerFrag);


        tabAdapter = new FragmentTabAdapter(HomeActivity.this, fragments_list, R.id.tab_content, mRadioGroup);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                currIndex = index;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("AutoFetchOrdersService", "unbindService");
        unbindService(serviceConnection);
    }

    //发送自动刷单语音提示
    private void sendNotificationForAutoOrders(boolean flag){
        if(mNotificationManager==null){
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentTitle("自动刷单模式变更通知");

        if(flag) {
            mBuilder.setContentText("已开启");
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.auto_order_on));
        }else {
            mBuilder.setContentText("已关闭");
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.auto_order_off));
        }

        Random ran =new Random(System.currentTimeMillis());
        final int notifyId  = ran.nextInt();
        Log.d(TAG,"notifyId = "+notifyId);
        mNotificationManager.notify(notifyId, mBuilder.build());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                mNotificationManager.cancel(notifyId);
            }
        }, 10*1000);
    }

}
