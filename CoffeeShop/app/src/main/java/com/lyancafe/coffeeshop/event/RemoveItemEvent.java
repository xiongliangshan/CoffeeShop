package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2017/8/14.
 */

public class RemoveItemEvent {

    public int tabIndex;
    public long orderId;

    public RemoveItemEvent(int tabIndex, long orderId) {
        this.tabIndex = tabIndex;
        this.orderId = orderId;
    }
}
