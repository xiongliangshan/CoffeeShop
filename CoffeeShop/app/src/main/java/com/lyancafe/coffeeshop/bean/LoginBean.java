package com.lyancafe.coffeeshop.bean;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.Jresp;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/21.
 */
public class LoginBean {

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
    public static LoginBean parseJsonLoginBean(Context context,Jresp resp){
        int shopId = resp.data.optInt("shopId");
        int userId = resp.data.optInt("userId");
        String shopName = resp.data.optString("shopName");
        String token = resp.data.optString("token");
        boolean isSFMode = resp.data.optBoolean("isSFMode");
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
