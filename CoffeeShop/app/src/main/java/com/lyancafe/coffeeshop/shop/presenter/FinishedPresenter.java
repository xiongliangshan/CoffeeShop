package com.lyancafe.coffeeshop.shop.presenter;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface FinishedPresenter {


    //加载订单一览列表
    void loadFinishedOrders(long lastOrderId,boolean isLoadMore);

    //加载单量和杯量数据
    void loadOrderAmount();


}
