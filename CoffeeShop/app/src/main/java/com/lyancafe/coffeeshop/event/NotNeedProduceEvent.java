package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2017/8/31.
 */

public class NotNeedProduceEvent {
    public OrderBean order;

    public NotNeedProduceEvent(OrderBean order) {
        this.order = order;
    }
}
