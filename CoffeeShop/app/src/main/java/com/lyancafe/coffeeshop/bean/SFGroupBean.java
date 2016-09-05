package com.lyancafe.coffeeshop.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SFGroupBean {

    private List<OrderBean> orderGroup;

    public SFGroupBean() {
    }

    public List<OrderBean> getOrderGroup() {
        return orderGroup;
    }

    public void setOrderGroup(List<OrderBean> orderGroup) {
        this.orderGroup = orderGroup;
    }


    @Override
    public String toString() {
        return "SFGroupBean{" +
                "orderGroup=" + orderGroup +
                '}';
    }
}
