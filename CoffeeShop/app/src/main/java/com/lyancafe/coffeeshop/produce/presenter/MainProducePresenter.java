package com.lyancafe.coffeeshop.produce.presenter;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface MainProducePresenter {

    //订单撤回请求
    void doRecallOrder(final long orderId);

}
