package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/10.
 */

public class OrderIdComparator implements Comparator<OrderBean> {

    @Override
    public int compare(OrderBean o1, OrderBean o2) {
        return (int) (o1.getId()-o2.getId());
    }
}
