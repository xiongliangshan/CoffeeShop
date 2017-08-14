package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2017/8/14.
 */

public class RevokeEvent {

    public long orderId;

    public RevokeEvent(long orderId) {
        this.orderId = orderId;
    }
}
