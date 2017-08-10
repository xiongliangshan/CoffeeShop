package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.BaseObserver;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceModel{

    //加载列表数据
    void loadToProduceOrders(int shopId, String token, BaseObserver<List<OrderBean>> observer);


    //开始生产
    void doStartProduce(int shopId, long orderId, String token, Observer<BaseEntity<JsonObject>> observer);


    //批量开始生产
    void doStartBatchProduce(int shopId,List<Long> orderIds,String token,Observer<BaseEntity<JsonObject>> observer);

}