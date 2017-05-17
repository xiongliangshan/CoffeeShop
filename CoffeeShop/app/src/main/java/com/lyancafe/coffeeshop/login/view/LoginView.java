package com.lyancafe.coffeeshop.login.view;

/**
* Created by Administrator on 2017/03/13
*/

public interface LoginView{

    String getUserName();
    String getPassword();
    void stepToMain();
    void showLoadingDlg();
    void dismissLoadingDlg();
}