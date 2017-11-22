package com.lyancafe.coffeeshop.setting.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;

/**
 * Created by Administrator on 2017/8/16.
 */

public interface SettingView extends BaseView{

    void showUpdateConfirmDlg(final ApkInfoBean apk);


   /* void showToast(String message);

    //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();*/
}
