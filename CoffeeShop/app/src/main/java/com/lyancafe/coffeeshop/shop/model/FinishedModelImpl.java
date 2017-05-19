package com.lyancafe.coffeeshop.shop.model;


import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.RetrofitHttp;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedModelImpl implements FinishedModel{


    @Override
    public void loadFinishedOrders(int shopId, long orderId, String token, Observer<BaseEntity<List<OrderBean>>> observer) {
        RetrofitHttp.getRetrofit().loadFinishedOrders(shopId,orderId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void loadOrderAmount(int shopId, String token, Observer<BaseEntity<JsonObject>> observer) {
        RetrofitHttp.getRetrofit().loadOrderAmount(shopId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}