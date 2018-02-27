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
    private boolean needPrintTime;
    private String drinkGuide;
    private boolean autoFlag = true;

    public UserBean() {
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

    public boolean isNeedPrintTime() {
        return needPrintTime;
    }

    public void setNeedPrintTime(boolean needPrintTime) {
        this.needPrintTime = needPrintTime;
    }

    public String getDrinkGuide() {
        return drinkGuide;
    }

    public void setDrinkGuide(String drinkGuide) {
        this.drinkGuide = drinkGuide;
    }

    public boolean isAutoFlag() {
        return autoFlag;
    }

    public void setAutoFlag(boolean autoFlag) {
        this.autoFlag = autoFlag;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userId=" + userId +
                ", shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", token='" + token + '\'' +
                ", needPrintTime=" + needPrintTime +
                ", drinkGuide='" + drinkGuide + '\'' +
                ", autoFlag=" + autoFlag +
                '}';
    }
}
