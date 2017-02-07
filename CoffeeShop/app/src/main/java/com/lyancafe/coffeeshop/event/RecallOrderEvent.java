package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2017/1/20.
 */

public class RecallOrderEvent {
    public int tabIndex;
    public int orderId;

    public RecallOrderEvent(int tabIndex, int orderId) {
        this.tabIndex = tabIndex;
        this.orderId = orderId;
    }
}
