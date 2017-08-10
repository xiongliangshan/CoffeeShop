package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.BaseObserver;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducedModel {


    //加载列表数据
    void loadToFetchOrders(int shopId, String token, BaseObserver<List<OrderBean>> observer);

}