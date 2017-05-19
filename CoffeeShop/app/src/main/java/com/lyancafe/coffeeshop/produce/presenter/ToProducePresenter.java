package com.lyancafe.coffeeshop.produce.presenter;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface ToProducePresenter {

    //加载待生产列表
    void loadToProduceOrders();

    //点击开始生产
    void doStartProduce(long orderId);

}
