package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;

import java.util.Comparator;

/**
 * @author yangjz 2018/3/22.
 */

public class OrderSortInstanceComparator implements Comparator<OrderBean> {

    @Override
    public int compare(OrderBean o1, OrderBean o2) {
        long deltaInstanceTime = o1.getInstanceTime() - o2.getInstanceTime();
        if (deltaInstanceTime < 0) {
            return -1;
        } else if (deltaInstanceTime > 0) {
            return 1;
        } else {
            int deltaQutity = OrderHelper.getTotalQutity(o2) - OrderHelper.getTotalQutity(o1);
            if (deltaQutity != 0) {
                return deltaQutity;
            } else {
                return o1.getShopOrderNo() - o2.getShopOrderNo();
            }
        }
    }
}
