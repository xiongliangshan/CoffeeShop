package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/11/16.
 */
public class NewOderComingEvent {
    public long orderId;

    public NewOderComingEvent(long orderId) {
        this.orderId = orderId;
    }
}
