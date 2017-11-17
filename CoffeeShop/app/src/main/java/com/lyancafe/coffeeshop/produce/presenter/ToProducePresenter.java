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
    void doStartProduce(long orderId,boolean isScanCode);

    //点击批量开始生产
    void doStartBatchProduce(List<OrderBean> orders);

    //无需生产
    void doNoPruduce(long orderId);

}
