package com.lyancafe.coffeeshop.login.model;

import android.app.Activity;
import android.content.Context;

/**
* Created by Administrator on 2017/03/13
*/

public interface LoginModel{

    //登录
    void login(Activity activity,String userName, String password,final LoginModelImpl.OnHandleLoginListener listener);


    //上传设备信息
    void uploadDeviceInfo(String regId, LoginModelImpl.OnHandleUpLoadDeviceInfoListener listener);

    //判断是否是当天第一次登录
    boolean isCurrentDayFirstLogin(Context context);
}