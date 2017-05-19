package com.lyancafe.coffeeshop.produce.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingModel{

    //加载生产中数据列表
    void loadProducingOrders(int shopId, String token, Observer<BaseEntity<List<OrderBean>>> observer);

    //上产完成
    void dodoFinishProduced(int shopId, long orderId, String token, Observer<BaseEntity<JsonObject>> observer);
}