package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

/**
 * Created by Administrator on 2016/7/28.
 */
public class FinishProduceEvent {

    public OrderBean order;

    public FinishProduceEvent(OrderBean order) {
        this.order = order;
    }
}
