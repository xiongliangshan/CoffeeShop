package com.lyancafe.coffeeshop.delivery.model;


import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.CourierBean;
import com.lyancafe.coffeeshop.http.RetrofitHttp;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
* Created by Administrator on 2017/03/16
*/

public class CourierModelImpl implements CourierModel{


    @Override
    public void loadCouriers(int shopId, String token, Observer<BaseEntity<List<CourierBean>>> observer) {
        RetrofitHttp.getRetrofit().loadCouriers(shopId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}