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
     */
    void login(Map<String,Object> params,Observer<BaseEntity<UserBean>> Observer);



}