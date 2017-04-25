package com.lyancafe.coffeeshop.shop.model;

/**
* Created by Administrator on 2017/03/17
*/

public interface FinishedModel{

    //加载订单一览列表
    void loadFinishedOrders(long lastOrderId,FinishedModelImpl.OnHandleResponseListener listener);


    //加载单量和杯量数据
    void loadOrderAmounts(FinishedModelImpl.OnHandleResponseListener listener);

    /*//加载服务时效百分比
    void loadEffectPercent(FinishedModelImpl.OnHandleResponseListener listener);*/
}