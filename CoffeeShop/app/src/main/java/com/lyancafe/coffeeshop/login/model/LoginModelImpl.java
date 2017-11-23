package com.lyancafe.coffeeshop.login.model;


import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import io.reactivex.Observer;

import static com.lyancafe.coffeeshop.common.LoginHelper.getLoginTime;
import static com.lyancafe.coffeeshop.common.LoginHelper.saveCurrentDayFirstLoginTime;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginModelImpl implements LoginModel{

    private static final String TAG = "login";

    @Override
    public void login(String loginName, String password,String regId,Observer<BaseEntity<UserBean>> observer) {
        RetrofitHttp.getRetrofit().login(loginName,password,regId)
                .compose(RxHelper.<BaseEntity<UserBean>>io_main())
                .subscribe(observer);
    }


    @Override
    public void uploadDeviceInfo(int shopId,int userId,Map<String,Object> params, Observer<BaseEntity> observer) {
        RetrofitHttp.getRetrofit().uploadDeviceInfo(shopId,userId,params)
                .compose(RxHelper.<BaseEntity>io_main())
                .subscribe(observer);
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