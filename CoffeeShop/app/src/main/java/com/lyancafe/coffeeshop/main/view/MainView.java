package com.lyancafe.coffeeshop.main.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;

/**
* Created by Administrator on 2017/03/14
*/

public interface MainView{


    //切换fragment
    void switchFragment(int selectedIndex);


    //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();


    void showUpdateConfirmDlg(final ApkInfoBean apk);


    void showToast(String message);
}