package com.lyancafe.coffeeshop.setting.presenter;

/**
 * Created by Administrator on 2017/8/16.
 */

public interface SettingPresenter {

    //检测版本更新
    void checkUpdate(boolean isShowProgress);

    //退出登录
    void exitLogin();
}
