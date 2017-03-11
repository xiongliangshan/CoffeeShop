package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/3/11.
 */

public class DeliverBean {
    private int id;
    private String name;
    private String phone;
    private int totalOrderCount;
    private int deliveringOrderCount;
    private double distanceToShop;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(int totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public int getDeliveringOrderCount() {
        return deliveringOrderCount;
    }

    public void setDeliveringOrderCount(int deliveringOrderCount) {
        this.deliveringOrderCount = deliveringOrderCount;
    }

    public double getDistanceToShop() {
        return distanceToShop;
    }

    public void setDistanceToShop(double distanceToShop) {
        this.distanceToShop = distanceToShop;
    }


    @Override
    public String toString() {
        return "DeliverBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", totalOrderCount=" + totalOrderCount +
                ", deliveringOrderCount=" + deliveringOrderCount +
                ", distanceToShop=" + distanceToShop +
                '}';
    }
}
