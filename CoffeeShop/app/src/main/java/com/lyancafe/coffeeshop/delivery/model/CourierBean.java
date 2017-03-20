package com.lyancafe.coffeeshop.delivery.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.bean.XlsResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/11.
 */

public class CourierBean {
    //小哥id
    private int id;

    //小哥姓名
    private String name;

    //小哥手机号码
    private String phone;

    //手上今天各种状态下的总单量
    private int totalOrderCount;

    //配送中的单量
    private int deliveringOrderCount;

    //距离门店的距离
    private double distanceToShop;

    //小哥所在位置的纬度
    private double lat;

    //小哥所在位置的经度
    private double lng;


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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistanceToShop() {
        return distanceToShop;
    }

    public void setDistanceToShop(double distanceToShop) {
        this.distanceToShop = distanceToShop;
    }


    @Override
    public String toString() {
        return "CourierBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", totalOrderCount=" + totalOrderCount +
                ", deliveringOrderCount=" + deliveringOrderCount +
                ", distanceToShop=" + distanceToShop +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }


    public static List<CourierBean> parseJsonToCouriers(XlsResponse resp){
        List<CourierBean> list = new ArrayList<>();
        if(resp==null || resp.data==null){
            return list;
        }
        try {
            JSONArray jsonArray = resp.data.getJSONArray("couriers");
            if(jsonArray!=null){
                list = JSON.parseArray(jsonArray.toString(),CourierBean.class);
            }
        }catch (JSONException e){
            Log.e("json","parseJsonToCouriers ,解析失败");
        }
        return list;
    }
}
