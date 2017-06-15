package com.lyancafe.coffeeshop.shop.presenter;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface ExceptionalPresenter {

    void loadExceptionalOrders();

    void doRePush(long orderId,int deliverTeam);
}
