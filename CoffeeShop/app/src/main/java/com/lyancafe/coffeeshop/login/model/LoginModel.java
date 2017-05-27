package com.lyancafe.coffeeshop.login.model;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;

import java.util.Map;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/13
*/

public interface LoginModel{


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
     * @param observer
     */
    void uploadDeviceInfo(int shopId,int userId,Map<String,Object> params, Observer<BaseEntity> observer);



}