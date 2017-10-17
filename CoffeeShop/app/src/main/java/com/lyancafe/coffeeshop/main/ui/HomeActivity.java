package com.lyancafe.coffeeshop.main.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseActivity;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.main.presenter.MainPresenter;
import com.lyancafe.coffeeshop.main.presenter.MainPresenterImpl;
import com.lyancafe.coffeeshop.main.view.MainView;
import com.lyancafe.coffeeshop.produce.ui.MainProduceFragment;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.setting.ui.SettingFragment;
import com.lyancafe.coffeeshop.shop.ui.MainShopFragment;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeActivity extends BaseActivity implements MainView {

    private static final String TAG = "main";
    public List<Fragment> fragmentsList = new ArrayList<Fragment>();

    private MainProduceFragment orderFrag;
    private MainShopFragment shopFragment;
    private SettingFragment settingFragment;
    private Context context;
    private int mSelectedIndex = 0;

    private MainPresenter mMainPresenter;
    private LoadingDialog mLoadingDlg;

    @BindView(R.id.ll_produce_tab)
    LinearLayout tabProduceLayout;
    @BindView(R.id.ll_shop_tab)
    LinearLayout tabShopLayout;
    @BindView(R.id.ll_system_tab)
    LinearLayout llSystemTab;
    @BindViews({R.id.ll_produce_tab, R.id.ll_shop_tab,R.id.ll_system_tab})
    List<LinearLayout> tabList;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //   super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = HomeActivity.this;
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenterImpl(this, this);
        initViews();
        updateTab(mSelectedIndex);
        Intent intent = new Intent(HomeActivity.this, TaskService.class);
        startService(intent);
        ClassLoader classLoader = HomeActivity.class.getClassLoader();
        if(classLoader!=null){
            LogUtil.d(TAG,"classloader = "+classLoader.toString());
        }

    }

    private void initViews() {
        orderFrag = new MainProduceFragment();
        shopFragment = new MainShopFragment();
        settingFragment = new SettingFragment();
        fragmentsList.add(orderFrag);
        fragmentsList.add(shopFragment);
        fragmentsList.add(settingFragment);
    }


    @Override
    public void showLoading() {
        if (mLoadingDlg == null) {
            mLoadingDlg = new LoadingDialog(this);
        }
        if (!mLoadingDlg.isShowing()) {
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void showToast(String message) {
        ToastUtil.show(getApplicationContext(), message);
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
                intent.putExtra("apk", apk);
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
        super.onDestroy();
    }


    @OnClick({R.id.ll_produce_tab, R.id.ll_shop_tab,R.id.ll_system_tab})
    void onLeftTabClick(View v) {
        switch (v.getId()) {
            case R.id.ll_produce_tab:
                mSelectedIndex = 0;
                break;
            case R.id.ll_shop_tab:
                mSelectedIndex = 1;
                break;
            case R.id.ll_system_tab:
                mSelectedIndex = 2;
                break;
        }
        updateTab(mSelectedIndex);

    }


    private void updateTab(int selectedIndex) {
        for (int i = 0; i < tabList.size(); i++) {
            LinearLayout linearLayout = tabList.get(i);
            View childView = linearLayout.getChildAt(0);
            if (selectedIndex == i) {
                linearLayout.setBackgroundColor(context.getResources().getColor(R.color.black1));
                ((TextView) childView).setTextColor(context.getResources().getColor(R.color.yellow1));
            } else {
                linearLayout.setBackgroundColor(context.getResources().getColor(R.color.gray4));
                ((TextView) childView).setTextColor(context.getResources().getColor(R.color.gray5));
            }
        }
        switchFragment(selectedIndex);
    }


    @Override
    public void switchFragment(int selectedIndex) {
        Fragment fragment = fragmentsList.get(selectedIndex);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.tab_content, fragment);
            ft.commitAllowingStateLoss();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof MainProduceFragment) {
            ft.hide(shopFragment).hide(settingFragment).show(orderFrag);
        } else if (fragment instanceof MainShopFragment) {
            ft.hide(orderFrag).hide(settingFragment).show(shopFragment);
        } else {
            ft.hide(orderFrag).hide(shopFragment).show(settingFragment);
        }
        ft.commitAllowingStateLoss();
        fragment.onResume();
    }

}
