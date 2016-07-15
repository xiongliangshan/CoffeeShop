package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/7/15.
 */
public class UpdatePrintStatusEvent {

    public String orderSn;

    public UpdatePrintStatusEvent(String orderSn) {
        this.orderSn = orderSn;
    }
}
