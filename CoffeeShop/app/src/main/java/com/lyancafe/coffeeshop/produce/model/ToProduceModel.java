package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceModel{

    //加载列表数据
    void loadToProduceOrders(int shopId, String token, CustomObserver<List<OrderBean>> observer);


    //开始生产
    void doStartProduce(int shopId, long orderId, String token, CustomObserver<JsonObject> observer);


    //批量开始生产
    void doStartBatchProduce(int shopId,List<Long> orderIds,String token,CustomObserver<JsonObject> observer);


    //无需生产
    void doNoProduce(int shopId,long orderId,String token,CustomObserver<JsonObject> observer);

}