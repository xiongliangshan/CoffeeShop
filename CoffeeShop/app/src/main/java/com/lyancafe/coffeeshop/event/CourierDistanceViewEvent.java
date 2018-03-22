package com.lyancafe.coffeeshop.event;

/**
 * @author yangjz 2018/3/21.
 */

public class CourierDistanceViewEvent {

    public long orderId;
    public CourierDistanceViewEvent(long orderId){
        this.orderId = orderId;
    }
}
