package com.lyancafe.coffeeshop.login.model;


import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.http.RetrofitHttp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.lyancafe.coffeeshop.common.LoginHelper.getLoginTime;
import static com.lyancafe.coffeeshop.common.LoginHelper.saveCurrentDayFirstLoginTime;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginModelImpl implements LoginModel{

    private static final String TAG = "login";

    @Override
    public void login(String loginName, String password, Observer<BaseEntity<UserBean>> observer) {
        RetrofitHttp.getRetrofit().login(loginName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    @Override
    public void uploadDeviceInfo(int shopId,int userId,Map<String,Object> params, Consumer<BaseEntity> consumer) {
        RetrofitHttp.getRetrofit().uploadDeviceInfo(shopId,userId,params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    @Override
    public boolean isCurrentDayFirstLogin(Context context) {
        long currentTime = System.currentTimeMillis();
        long firstTime = getLoginTime(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time1 = sdf.format(new Date(firstTime));
        Log.d(TAG,"time 1 ="+time1);
        String time2 = sdf.format(new Date(currentTime));
        if(time1.equals(time2)){
            Log.d(TAG,"not current day first login");
            return false;
        }else{
            saveCurrentDayFirstLoginTime(context,currentTime);
            Log.d(TAG, "is first login");
            return true;
        }
    }

}