package com.lyancafe.coffeeshop.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.fragment.ShopManagerFragment;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.SettingWindow;
import com.lzy.okgo.OkGo;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private SettingWindow sw;
    private TaskService taskService;
    private ServiceConnection serviceConnection;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context  = HomeActivity.this;

        setContentView(R.layout.activity_home);
        initViews();
        initFragments();
        //启动service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("TaskService","onServiceConnected");
                TaskService.MyBinder myBinder = (TaskService.MyBinder)service;
                taskService = myBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("TaskService","onServiceDisconnected");
            }
        };
        Intent intent=new Intent(HomeActivity.this,TaskService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("TaskService", "bindService");

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


        new CSApplication.CheckUpdateQry(context, MyUtil.getVersionCode(context),false).doRequest();
    }

    private void initViews(){
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
        Log.d("TaskService", "unbindService");


    }


}
