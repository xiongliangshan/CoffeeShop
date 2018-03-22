package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BatchOrder;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceModel{

    //加载列表数据
    void loadToProduceOrders(int shopId,CustomObserver<List<OrderBean>> observer);


    //开始生产
    void doStartProduce(int shopId, long orderId,CustomObserver<JsonObject> observer);


    //批量开始生产
    void doStartBatchProduce(int shopId, BatchOrder batchOrder, CustomObserver<JsonObject> observer);


    //无需生产
    void doNoProduce(int shopId,long orderId,CustomObserver<JsonObject> observer);

    //加载最近单量与杯量
    void loadLatelyCount(int shopId, CustomObserver<JsonObject> observer);

}