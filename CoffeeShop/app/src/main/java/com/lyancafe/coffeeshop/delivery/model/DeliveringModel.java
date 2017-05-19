package com.lyancafe.coffeeshop.delivery.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/16
*/

public interface DeliveringModel{

    //加载列表数据
    void loadDeliveringOrders(int shopId, String token, Observer<BaseEntity<List<OrderBean>>> observer);
}