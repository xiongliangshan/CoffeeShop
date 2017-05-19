package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceModel{

    //加载列表数据
    void loadToProduceOrders(int shopId, String token, Observer<BaseEntity<List<OrderBean>>> observer);


    //开始生产
    void doStartProduce(int shopId, long orderId, String token, Observer<BaseEntity<JsonObject>> observer);

}