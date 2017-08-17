package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingModel{

    //加载生产中数据列表
    void loadProducingOrders(int shopId, String token, CustomObserver<List<OrderBean>> observer);

    //上产完成
    void dodoFinishProduced(int shopId, long orderId, String token, CustomObserver<JsonObject> observer);

    //批量完成生产
    void doCompleteBatchProduce(int shopId,List<Long> orderIds,String token,CustomObserver<JsonObject> observer);
}