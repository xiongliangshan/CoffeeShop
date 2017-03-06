package com.lyancafe.coffeeshop.bean;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class TimeEffectBean {

    //门店单号
    private int shopOrderNo;

    //订单号
    private int orderId;

    //0:全部,  1：良好，2：及格，3：不及格
    private int levleType;

    //订单类型 0:预约单 1:及时单
    private int instant;

    //配送团队
    private int deliverTeam;

    //下单时间
    private long orderTime;

    //期望送达时间
    private long exceptedTime;

    //生产完成时间
    private long producedTime;

    //抢单时间
    private long grabTime;

    //取货时间
    private long fetchTime;

    //送达时间
    private long deliveredTime;

    //小哥姓名
    private String deliverName;


    public int getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(int shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getLevleType() {
        return levleType;
    }

    public void setLevleType(int levleType) {
        this.levleType = levleType;
    }

    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
    }

    public int getDeliverTeam() {
        return deliverTeam;
    }

    public void setDeliverTeam(int deliverTeam) {
        this.deliverTeam = deliverTeam;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getExceptedTime() {
        return exceptedTime;
    }

    public void setExceptedTime(long exceptedTime) {
        this.exceptedTime = exceptedTime;
    }

    public long getProducedTime() {
        return producedTime;
    }

    public void setProducedTime(long producedTime) {
        this.producedTime = producedTime;
    }

    public long getGrabTime() {
        return grabTime;
    }

    public void setGrabTime(long grabTime) {
        this.grabTime = grabTime;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public long getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(long deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getDeliverName() {
        return deliverName;
    }

    public void setDeliverName(String deliverName) {
        this.deliverName = deliverName;
    }

    @Override
    public String toString() {
        return "TimeEffectBean{" +
                "shopOrderNo=" + shopOrderNo +
                ", orderId=" + orderId +
                ", levleType=" + levleType +
                ", instant=" + instant +
                ", deliverTeam=" + deliverTeam +
                ", orderTime=" + orderTime +
                ", exceptedTime=" + exceptedTime +
                ", producedTime=" + producedTime +
                ", grabTime=" + grabTime +
                ", fetchTime=" + fetchTime +
                ", deliveredTime=" + deliveredTime +
                ", deliverName='" + deliverName + '\'' +
                '}';
    }


    //解析数据
    public  static List<TimeEffectBean> parseJsonOrders(Context context, XlsResponse resp){
        List<TimeEffectBean> timeEffectBeanList = new ArrayList<>();
        if(resp==null || resp.data==null){
            return timeEffectBeanList;
        }
        try{
            com.alibaba.fastjson.JSONArray ordersArray= resp.data.getJSONArray("orders");
            if(ordersArray!=null){
                timeEffectBeanList = JSON.parseArray(ordersArray.toString(), TimeEffectBean.class);
            }
        }catch (JSONException e){
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return timeEffectBeanList;
    }
}
