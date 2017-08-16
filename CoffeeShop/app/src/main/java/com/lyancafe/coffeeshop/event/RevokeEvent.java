package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2017/8/14.
 */

public class RevokeEvent {

    public OrderBean orderBean;

    public RevokeEvent(OrderBean orderBean) {
        this.orderBean = orderBean;
    }
}
