package com.lyancafe.coffeeshop.produce.presenter;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface ToProducePresenter {

    //加载待生产列表
    void loadToProduceOrders();

    //点击开始生产
    void doStartProduce(OrderBean order,boolean isAuto);

    //点击批量开始生产
    void doStartBatchProduce(List<OrderBean> orders);

    //无需生产
    void doNoPruduce(long orderId);

    //小哥距离
    void loadCourierDistance(long orderId);
}
