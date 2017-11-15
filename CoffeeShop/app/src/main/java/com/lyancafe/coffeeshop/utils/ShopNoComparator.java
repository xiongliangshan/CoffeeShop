package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/11/15.
 */

public class ShopNoComparator implements Comparator<OrderBean> {

    @Override
    public int compare(OrderBean o1, OrderBean o2) {
        return o1.getShopOrderNo()-o2.getShopOrderNo();
    }
}
