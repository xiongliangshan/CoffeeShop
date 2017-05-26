package com.lyancafe.coffeeshop.main.presenter;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface MainPresenter {

    //检测版本更新
    void checkUpdate(boolean isShowProgress);

    //退出登录
    void exitLogin();

}
