package com.lyancafe.coffeeshop.shop.presenter;

import com.lyancafe.coffeeshop.shop.model.FinishedModelImpl;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface FinishedPresenter {


    //加载订单一览列表
    void loadFinishedOrders(long lastOrderId);

    //加载单量和杯量数据
    void loadOrderAmounts();

    //加载服务时效百分比
    void loadEffectPercent();
}
