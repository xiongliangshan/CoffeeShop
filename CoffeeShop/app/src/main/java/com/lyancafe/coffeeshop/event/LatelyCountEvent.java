package com.lyancafe.coffeeshop.event;

/**
 * Created by wangqiaobo on 2018/3/21.
 */

public class LatelyCountEvent {

    private String latelyMin;

    private String orderNum;

    private String orderCups;

    public LatelyCountEvent(String latelyMin, String orderNum, String orderCups) {
        this.latelyMin = latelyMin;
        this.orderNum = orderNum;
        this.orderCups = orderCups;
    }

    public String getLatelyMin() {
        return latelyMin;
    }

    public void setLatelyMin(String latelyMin) {
        this.latelyMin = latelyMin;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderCups() {
        return orderCups;
    }

    public void setOrderCups(String orderCups) {
        this.orderCups = orderCups;
    }
}
