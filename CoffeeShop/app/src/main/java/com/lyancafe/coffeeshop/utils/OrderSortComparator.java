package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/8.
 * 依次按预期送达时间，杯量，门店单号排序
 */

public class OrderSortComparator implements Comparator<OrderBean> {

    @Override
    public int compare(OrderBean o1, OrderBean o2) {

        long deltaExpectedTime = o1.getExpectedTime()-o2.getExpectedTime();
        if(deltaExpectedTime<0){
            return -1;
        }else if(deltaExpectedTime>0){
            return 1;
        }else{
            int deltaQutity = OrderHelper.getTotalQutity(o2)-OrderHelper.getTotalQutity(o1);
            if(deltaQutity!=0){
                return deltaQutity;
            }else{
                return o1.getShopOrderNo()-o2.getShopOrderNo();
            }
        }

    }



}
