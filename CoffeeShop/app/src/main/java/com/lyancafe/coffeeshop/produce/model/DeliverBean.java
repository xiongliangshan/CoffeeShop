package com.lyancafe.coffeeshop.produce.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.bean.XlsResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DeliverBean {

    private long userId;     //小哥id
    private String name;    //小哥姓名
    private int type;       //小哥职位类型,1-全职；2-兼职；3-第三方小哥
    private boolean shopAccount; //是否是门店账号，
    private int distance;    //小哥离门店的距离,单位米
    private int orderCount;   //小哥手上未完成订单数量

    public DeliverBean() {
    }

    public DeliverBean(long userId, String name, int type, boolean shopAccount, int distance, int orderCount) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.shopAccount = shopAccount;
        this.distance = distance;
        this.orderCount = orderCount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    @Override
    public String toString() {
        return "CourierBean{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", shopAccount=" + shopAccount +
                ", distance=" + distance +
                ", orderCount=" + orderCount +
                '}';
    }


    public static List<DeliverBean> parseJsonToDelivers(XlsResponse resp){
        List<DeliverBean> list = new ArrayList<>();
        if(resp==null || resp.data==null){
            return list;
        }
        try {
            JSONArray jsonArray = resp.data.getJSONArray("couriers");
            list = JSON.parseArray(jsonArray.toString(),DeliverBean.class);
        }catch (JSONException e){
            Log.e("json","parseJsonToDelivers ,解析失败");
        }
        return list;
    }
}
