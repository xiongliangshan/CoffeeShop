package com.lyancafe.coffeeshop.main.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseActivity;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.delivery.ui.MainDeliverFragment;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.shop.ui.MainShopFragment;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.main.presenter.MainPresenter;
import com.lyancafe.coffeeshop.main.presenter.MainPresenterImpl;
import com.lyancafe.coffeeshop.main.view.MainView;
import com.lyancafe.coffeeshop.produce.ui.MainProduceFragment;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;
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

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeActivity extends BaseActivity implements MainView{

    private static final String TAG ="main";
    public List<Fragment> fragmentsList = new ArrayList<Fragment>();
    private MainProduceFragment orderFrag;
    private MainDeliverFragment deliverFragment;
    private MainShopFragment shopFragment;
    private TaskService taskService;
    private ServiceConnection connection;
    private Context context;
    private int mSelectedIndex = 0;

    private MainPresenter mMainPresenter;
    private LoadingDialog mLoadingDlg;

    @BindView(R.id.ll_produce_tab) LinearLayout tabProduceLayout;
    @BindView(R.id.ll_deliver_tab) LinearLayout tabDeliverlayout;
    @BindView(R.id.ll_shop_tab) LinearLayout tabShopLayout;
    @BindViews({R.id.ll_produce_tab,R.id.ll_deliver_tab,R.id.ll_shop_tab})
    List<LinearLayout> tabList;
    @BindView(R.id.tv_shop_name) TextView shopNameText;
    @BindView(R.id.tv_current_version) TextView curVerText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context  = HomeActivity.this;
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenterImpl(this,this);
        initViews();
        updateTab(mSelectedIndex);
        //启动service
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("TaskService","onServiceConnected");
                TaskService.MyBinder myBinder = (TaskService.MyBinder)service;
                taskService = myBinder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("TaskService","onServiceDisconnected");
                taskService = null;
            }
        };
        Intent intent=new Intent(HomeActivity.this,TaskService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);


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

        mMainPresenter.checkUpdate(false);

    }

    private void initViews(){
        shopNameText.setText(LoginHelper.getUser(context).getShopName());
        curVerText.setText(String.format("当前版本:%s", MyUtil.getVersion(context)));

        orderFrag =  new MainProduceFragment();
        deliverFragment = new MainDeliverFragment();
        shopFragment = new MainShopFragment();
        fragmentsList.add(orderFrag);
        fragmentsList.add(deliverFragment);
        fragmentsList.add(shopFragment);
    }


    @Override
    public void showLoading() {
        if(mLoadingDlg==null){
            mLoadingDlg = new LoadingDialog(this);
        }
        if(!mLoadingDlg.isShowing()){
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if(mLoadingDlg!=null && mLoadingDlg.isShowing()){
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void showToast(String message) {
        ToastUtil.showToast(getApplicationContext(),message);
    }

    @Override
    public void showUpdateConfirmDlg(final ApkInfoBean apk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_download, apk.getAppName()));
        builder.setTitle(getString(R.string.version_update));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //启动Service下载apk文件
                Intent intent = new Intent(HomeActivity.this, DownLoadService.class);
                intent.putExtra("apk",apk);
                startService(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.cacel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
        Log.d("TaskService", "unbindService");


    }


    @OnClick({R.id.ll_produce_tab,R.id.ll_deliver_tab,R.id.ll_shop_tab})
    void onLeftTabClick(View v) {
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
        }

    }

    @OnClick({R.id.tv_check_update,R.id.tv_login_out})
    void onClick(View v){
        switch (v.getId()){
            case R.id.tv_check_update:
                //系统更新
                mMainPresenter.checkUpdate(true);
                break;
            case R.id.tv_login_out:
                //退出登录
                mMainPresenter.exitLogin();
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


    @Override
    public void switchFragment(int selectedIndex) {
        Fragment fragment = fragmentsList.get(selectedIndex);
        if(!fragment.isAdded()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.tab_content,fragment);
            ft.commitAllowingStateLoss();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragment instanceof MainProduceFragment){
            ft.hide(deliverFragment).hide(shopFragment).show(orderFrag);
        }else if(fragment instanceof MainDeliverFragment){
            ft.hide(orderFrag).hide(shopFragment).show(deliverFragment);
        }else{
            ft.hide(orderFrag).hide(deliverFragment).show(shopFragment);
        }
        ft.commitAllowingStateLoss();
        fragment.onResume();
    }


}
