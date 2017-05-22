package com.lyancafe.coffeeshop.delivery.presenter;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface MainDeliverPresenter {
    //订单撤回请求
    void doRecallOrder(final long orderId);
}
