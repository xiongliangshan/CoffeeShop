package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.BaseObserver;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/17
*/

public interface FinishedModel{

    //加载订单一览列表
    void loadFinishedOrders(int shopId, long orderId, String token, BaseObserver<List<OrderBean>> observer);

    //加载订单单量和杯量
    void loadOrderAmount(int shopId, String token, BaseObserver<JsonObject> observer);
}