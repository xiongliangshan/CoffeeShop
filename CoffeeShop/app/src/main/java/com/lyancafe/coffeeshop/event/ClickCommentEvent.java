package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ClickCommentEvent {

    public OrderBean orderBean;

    public ClickCommentEvent(OrderBean orderBean) {
        this.orderBean = orderBean;
    }
}
