package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2016/7/29.
 */
public class UpdateFinishedOrderDetailEvent {

    public OrderBean orderBean;

    public UpdateFinishedOrderDetailEvent(OrderBean orderBean) {
        this.orderBean = orderBean;
    }
}
