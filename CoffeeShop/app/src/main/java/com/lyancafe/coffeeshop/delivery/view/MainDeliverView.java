package com.lyancafe.coffeeshop.delivery.view;

/**
* Created by Administrator on 2017/03/16
*/

public interface MainDeliverView{

    /**
     * 订单状态改变刷新列表
     * @param orderId
     * @param status 目标状态
     */
    void refreshListForStatus(long orderId,int status);

}