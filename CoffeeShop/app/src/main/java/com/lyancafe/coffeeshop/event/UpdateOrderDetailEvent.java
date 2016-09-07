package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2016/7/29.
 */
public class UpdateOrderDetailEvent {

    public OrderBean orderBean;

    public UpdateOrderDetailEvent(OrderBean orderBean) {
        this.orderBean = orderBean;
    }
}
