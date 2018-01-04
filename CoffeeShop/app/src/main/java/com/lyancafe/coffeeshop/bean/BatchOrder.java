package com.lyancafe.coffeeshop.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */

public class BatchOrder {

    private List<Long> orderIds;
    private List<Long> scanIds;

    public BatchOrder(List<Long> orderIds, List<Long> scanIds) {
        this.orderIds = orderIds;
        this.scanIds = scanIds;
    }

    @Override
    public String toString() {
        return "BatchOrder{" +
                "orderIds=" + orderIds +
                ", scanIds=" + scanIds +
                '}';
    }
}
