package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2016/7/19.
 */
public class CourierBean {

    private int userId;     //小哥id
    private String name;    //小哥姓名
    private int type;       //小哥职位类型
    private boolean shopAccount; //是否是门店账号
    private int distance;    //小哥离门店的距离
    private int orderCount;   //小哥手上未完成订单数量

    public CourierBean() {
    }

    public CourierBean(int userId, String name, int type, boolean shopAccount, int distance, int orderCount) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.shopAccount = shopAccount;
        this.distance = distance;
        this.orderCount = orderCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShopAccount() {
        return shopAccount;
    }

    public void setShopAccount(boolean shopAccount) {
        this.shopAccount = shopAccount;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
