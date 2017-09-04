package com.lyancafe.coffeeshop.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/21.
 */
public class UserBean implements Serializable{

    private int userId;
    private int shopId;
    private String shopName;
    private String token;

    public UserBean() {
    }

    public UserBean(int userId, int shopId, String shopName, String token) {
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "UserBean{" +
                "userId=" + userId +
                ", shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
