package com.lyancafe.coffeeshop.bean;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/21.
 */
public class LoginBean implements Serializable{

    private int userId;
    private int shopId;
    private String shopName;
    private boolean isSFMode;
    private String token;

    public LoginBean() {
    }

    public LoginBean(int userId, int shopId, String shopName, boolean isSFMode, String token) {
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.isSFMode = isSFMode;
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

    public boolean isSFMode() {
        return isSFMode;
    }

    public void setIsSFMode(boolean isSFMode) {
        this.isSFMode = isSFMode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //解析数据
    public static LoginBean parseJsonLoginBean(Context context,XlsResponse resp){
        int shopId = resp.data.getIntValue("shopId");
        int userId = resp.data.getIntValue("userId");
        String shopName = resp.data.getString("shopName");
        String token = resp.data.getString("token");
        boolean isSFMode = resp.data.getBoolean("isSFMode");
        LoginBean loginBean = new LoginBean(userId,shopId,shopName,isSFMode,token);
        return loginBean;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "userId=" + userId +
                ", shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", isSFMode=" + isSFMode +
                ", token='" + token + '\'' +
                '}';
    }
}
