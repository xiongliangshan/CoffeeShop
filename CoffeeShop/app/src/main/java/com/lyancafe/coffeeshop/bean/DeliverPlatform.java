package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/12/21.
 */

public class DeliverPlatform {

    private String name;
    private int orderCount;
    private int cupCount;

    public DeliverPlatform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getCupCount() {
        return cupCount;
    }

    public void setCupCount(int cupCount) {
        this.cupCount = cupCount;
    }



    @Override
    public String toString() {
        return "DeliverPlatform{" +
                "name='" + name + '\'' +
                ", orderCount=" + orderCount +
                ", cupCount=" + cupCount +
                '}';
    }
}
