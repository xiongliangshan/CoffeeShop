package com.lyancafe.coffeeshop.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.fragment.ShopManagerFragment;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.service.AutoFetchOrdersService;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.PropertiesUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.SettingWindow;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        File cacheDir = StorageUtils.getCacheDirectory(HomeActivity.this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(HomeActivity.this)
                .memoryCacheExtraOptions(800, 800) // default = device screen dimensions
                .diskCacheExtraOptions(800, 800, null)
//                .taskExecutor(...)
//                .taskExecutorForCachedImages(...)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);


        new CoffeeShopApplication.CheckUpdateQry(context, MyUtil.getVersionCode(context)).doRequest();
    }

   /* *//**
     * 检测新版本
     *//*
    private void checkNewVersion(){
        if(!PropertiesUtil.isNeedtoUpdate(context)){
            Log.d(TAG, "not need to update,return");
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.confirm_download, UpdateService.mNewestVersionName));
            builder.setTitle(context.getResources().getString(R.string.version_update));
            builder.setIcon(R.mipmap.app_icon);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //启动Service下载apk文件
                    Intent intent = new Intent(context, UpdateService.class);
                    intent.putExtra(UpdateService.KEY_TYPE, UpdateService.DOWNLOADAPK);
                    context.startService(intent);
                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.cacel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }*/

    private void initViews(){
        baristaLogoIV = (ImageView) findViewById(R.id.iv_barista_logo);
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
        InfoDetailDialog.getInstance(context).dismiss();
        unbindService(serviceConnection);
        super.onDestroy();
        Log.d("AutoFetchOrdersService", "unbindService");


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
