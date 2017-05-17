package com.lyancafe.coffeeshop.login.model;

import android.app.Activity;
import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;

import org.reactivestreams.Subscriber;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

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


    /**
     * 登录
     * @param loginName
     * @param password
     */
    void login(String loginName, String password, Observer<BaseEntity<UserBean>> Observer);


    /**
     * 上传设备信息
     * @param shopId
     * @param userId
     * @param params
     * @param consumer
     */
    void uploadDeviceInfo(int shopId,int userId,Map<String,Object> params, Consumer<BaseEntity> consumer);
}