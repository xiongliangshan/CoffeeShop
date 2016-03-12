package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/3/12.
 */
public class CancelOrderEvent {
    public long orderId;

    public CancelOrderEvent(long orderId) {
        this.orderId = orderId;
    }
}
