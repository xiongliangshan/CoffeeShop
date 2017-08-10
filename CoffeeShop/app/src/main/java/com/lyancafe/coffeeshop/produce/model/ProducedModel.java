package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducedModel {


    //加载列表数据
    void loadToFetchOrders(int shopId, String token, CustomObserver<List<OrderBean>> observer);

}