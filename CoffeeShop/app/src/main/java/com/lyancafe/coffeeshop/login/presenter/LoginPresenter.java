package com.lyancafe.coffeeshop.login.presenter;

/**
 * Created by Administrator on 2017/3/13.
 */

public interface LoginPresenter {

    //检查登录状态
    void checkLoginStatus();

    /**
     * 登录
     */
    void login();


    /**
     * 保存调试服务器IP地址
     */
    void saveDebugIp(String ip);


    /**
     * 获取调试ip
     */
    String getDebugIP();

    /**
     * 更新访问的IP地址
     */
    void updateUrl(String ip);
}
