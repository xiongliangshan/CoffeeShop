package com.lyancafe.coffeeshop.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.RefreshToFetchDataEvent;
import com.lyancafe.coffeeshop.fragment.DeliverFragment;
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.fragment.ShopManagerFragment;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener,DeliverFragment.OnFragmentInteractionListener{

    private static final String TAG ="HomeActivity";
    public List<Fragment> fragmentsList = new ArrayList<Fragment>();
    private OrdersFragment orderFrag;
//    private OrderQueryFragment orderQueryFrag;
    private DeliverFragment deliverFragment;
    private ShopManagerFragment shopManagerFrag;
    private TaskService taskService;
    private ServiceConnection serviceConnection;
    private Context context;

    private LinearLayout tabProduceLayout;
    private LinearLayout tabDeliverlayout;
    private LinearLayout tabShopLayout;

    private int mSelectedIndex = 0;
    private List<LinearLayout> tabList;

    private TextView shopNameText;
    private TextView curVerText;
    private TextView checkUpdateText;
    private TextView loginOutText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context  = HomeActivity.this;
        setContentView(R.layout.activity_home);
        initViews();
        initFragments();
        updateTab(mSelectedIndex);
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

        final File cacheDir = StorageUtils.getCacheDirectory(HomeActivity.this);
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


        HttpHelper.getInstance().reqCheckUpdate(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleCheckUpdateResponse(xlsResponse,call,response);
            }
        });
    }

    private void initViews(){
        tabProduceLayout = (LinearLayout) findViewById(R.id.ll_produce_tab);
        tabDeliverlayout = (LinearLayout) findViewById(R.id.ll_deliver_tab);
        tabShopLayout = (LinearLayout) findViewById(R.id.ll_shop_tab);
        shopNameText = (TextView) findViewById(R.id.tv_shop_name);
        curVerText = (TextView) findViewById(R.id.tv_current_version);
        checkUpdateText = (TextView) findViewById(R.id.tv_check_update);
        loginOutText = (TextView) findViewById(R.id.tv_login_out);

        checkUpdateText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        checkUpdateText.getPaint().setAntiAlias(true);

        loginOutText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        loginOutText.getPaint().setAntiAlias(true);


        tabProduceLayout.setOnClickListener(this);
        tabDeliverlayout.setOnClickListener(this);
        tabShopLayout.setOnClickListener(this);
        curVerText.setOnClickListener(this);
        checkUpdateText.setOnClickListener(this);
        loginOutText.setOnClickListener(this);

        shopNameText.setText(LoginHelper.getLoginBean(context).getShopName());
        curVerText.setText("当前版本:" + MyUtil.getVersion(context));

        tabList = new ArrayList<>();
        tabList.add(tabProduceLayout);
        tabList.add(tabDeliverlayout);
        tabList.add(tabShopLayout);



    }
    private void initFragments(){
        orderFrag =  new OrdersFragment();
//        orderQueryFrag = new OrderQueryFragment();
        deliverFragment = DeliverFragment.newInstance("","");
        shopManagerFrag = new ShopManagerFragment();
        fragmentsList.add(orderFrag);
    //    fragmentsList.add(orderQueryFrag);
        fragmentsList.add(deliverFragment);
        fragmentsList.add(shopManagerFrag);

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


    /**
     * 处理检测更新接口
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleCheckUpdateResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            final ApkInfoBean apkInfoBean = ApkInfoBean.parseJsonToBean(xlsResponse.data.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.confirm_download, apkInfoBean.getAppName()));
            builder.setTitle(context.getResources().getString(R.string.version_update));
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //启动Service下载apk文件
                    Intent intent = new Intent(context, UpdateService.class);
                    intent.putExtra("apk",apkInfoBean);
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
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_produce_tab:
                mSelectedIndex = 0;
                updateTab(mSelectedIndex);
                break;
            case R.id.ll_deliver_tab:
                mSelectedIndex = 1;
                updateTab(mSelectedIndex);
                break;
            case R.id.ll_shop_tab:
                mSelectedIndex = 2;
                updateTab(mSelectedIndex);
                break;
            case R.id.tv_check_update:
                //系统更新
                if (!MyUtil.isOnline(context)) {
                    ToastUtil.show(context, context.getResources().getString(R.string.check_internet));
                } else {
                    HttpHelper.getInstance().reqCheckUpdate(new DialogCallback<XlsResponse>(HomeActivity.this) {
                        @Override
                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                            handleCheckUpdateResponse(xlsResponse, call, response);
                        }
                    });
                }
                break;
            case R.id.tv_login_out:
                //退出登录
                HttpHelper.getInstance().reqLoginOut(new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleLoginOutResponse(xlsResponse,call,response);
                    }
                });
                LoginBean loginBean = LoginHelper.getLoginBean(context);
                loginBean.setToken("");
                LoginHelper.saveLoginBean(context,loginBean);
                OrderHelper.batchList.clear();
                HomeActivity.this.finish();
                HomeActivity.this.overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);
                break;
        }

    }

    private void updateTab(int selectedIndex){
        tabProduceLayout.setBackground(null);
        tabDeliverlayout.setBackground(null);
        tabShopLayout.setBackground(null);
        if(tabList!=null){
            tabList.get(selectedIndex).setBackgroundColor(context.getResources().getColor(R.color.tab_orange));
        }
        switchFragment(selectedIndex);
    }

    private void switchFragment(int selectedIndex){
        Fragment fragment = fragmentsList.get(selectedIndex);
        if(!fragment.isAdded()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.tab_content,fragment);
            ft.commitAllowingStateLoss();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragment instanceof OrdersFragment){
//            ft.hide(orderQueryFrag).hide(shopManagerFrag).show(orderFrag);
            ft.hide(deliverFragment).hide(shopManagerFrag).show(orderFrag);
        }else if(fragment instanceof DeliverFragment){
//            ft.hide(orderFrag).hide(shopManagerFrag).show(orderQueryFrag);
            ft.hide(orderFrag).hide(shopManagerFrag).show(deliverFragment);
        }else{
//            ft.hide(orderFrag).hide(orderQueryFrag).show(shopManagerFrag);
            ft.hide(orderFrag).hide(deliverFragment).show(shopManagerFrag);
        }
        ft.commitAllowingStateLoss();
        for(int i = 0;i<fragmentsList.size();i++){
            if(i==selectedIndex){
                fragmentsList.get(i).setUserVisibleHint(true);
            }else{
                fragmentsList.get(i).setUserVisibleHint(false);
            }
        }


    }

    /**
     * 处理退出登录
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleLoginOutResponse(XlsResponse xlsResponse, Call call, Response response) {
        Intent intent_update = new Intent(context, UpdateService.class);
        context.stopService(intent_update);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
