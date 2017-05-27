package com.lyancafe.coffeeshop.login.view;

import com.lyancafe.coffeeshop.bean.ApkInfoBean;

/**
* Created by Administrator on 2017/03/13
*/

public interface LoginView{

    String getUserName();
    String getPassword();
    void stepToMain();
    void showLoadingDlg();
    void dismissLoadingDlg();
    void showUpdateConfirmDlg(final ApkInfoBean apk);
}