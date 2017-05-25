package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/5/25.
 */

public class ShopInfo {
    private String shopName;
    private String shopAddress;
    private String shopTelephone;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopTelephone() {
        return shopTelephone;
    }

    public void setShopTelephone(String shopTelephone) {
        this.shopTelephone = shopTelephone;
    }

    @Override
    public String toString() {
        return "ShopInfo{" +
                "shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopTelephone='" + shopTelephone + '\'' +
                '}';
    }
}
