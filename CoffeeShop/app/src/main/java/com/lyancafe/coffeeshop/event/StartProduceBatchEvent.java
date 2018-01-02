package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class StartProduceBatchEvent {

    public List<OrderBean> orders;

    public StartProduceBatchEvent(List<OrderBean> orders) {
        this.orders = orders;
    }
}
