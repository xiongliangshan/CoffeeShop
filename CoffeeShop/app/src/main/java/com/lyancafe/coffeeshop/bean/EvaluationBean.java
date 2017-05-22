package com.lyancafe.coffeeshop.bean;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EvaluationBean {
    private int orderId;
    private int shopOrderNo;
    private int mtShopOrderNo;
    private int instant;
    private int deliveryTeam;   //   配送团队 4:lyan 5:qusong 6:wokuaidao 7:sweets 8:美团外卖 9:海葵
    private int type;           // 0:没有评价，4:好评，5:差评
    private List<String> tags;
    private String content;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(int shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public int getMtShopOrderNo() {
        return mtShopOrderNo;
    }

    public void setMtShopOrderNo(int mtShopOrderNo) {
        this.mtShopOrderNo = mtShopOrderNo;
    }

    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
    }

    public int getDeliveryTeam() {
        return deliveryTeam;
    }

    public void setDeliveryTeam(int deliveryTeam) {
        this.deliveryTeam = deliveryTeam;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "EvaluationBean{" +
                "orderId=" + orderId +
                ", shopOrderNo=" + shopOrderNo +
                ", mtShopOrderNo=" + mtShopOrderNo +
                ", instant=" + instant +
                ", deliveryTeam=" + deliveryTeam +
                ", type=" + type +
                ", tags=" + tags +
                ", content='" + content + '\'' +
                '}';
    }

}
