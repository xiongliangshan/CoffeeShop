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
     * 上传设备信息
     * @param token
     */
    void uploadDeviceInfo(int shopId,int userId,String token);
}
