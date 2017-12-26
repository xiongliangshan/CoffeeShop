package com.lyancafe.coffeeshop.bean;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/21.
 */
@Entity(indexes = {
        @Index(value = "product,orderId ASC", unique = true)
})
public class ItemContentBean implements Serializable,Comparable<ItemContentBean>{

    private static final long serialVersionUID = 33565800L;

    @NotNull
    private String product;        //咖啡名称
    private long orderId;          //对应订单的id
    private String unit;           //杯型
    private int price;             //单价,单位：分
    private int quantity;          //数量
    private int totalPrice;        //总价,单位：分
    private int coldHotProperty;   //1.冷  2.热  3.常温
    private String recipeFittings; //个性化标签
    private int produceProcess;    //0 ：null,1：咖啡师生产,咖啡师出品,2：饮品师生产，饮品师出品,3：咖啡师生产，饮品师生产，饮品师出品




    @Generated(hash = 81616558)
    public ItemContentBean(@NotNull String product, long orderId, String unit, int price,
            int quantity, int totalPrice, int coldHotProperty, String recipeFittings,
            int produceProcess) {
        this.product = product;
        this.orderId = orderId;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.coldHotProperty = coldHotProperty;
        this.recipeFittings = recipeFittings;
        this.produceProcess = produceProcess;
    }
    @Generated(hash = 1975170859)
    public ItemContentBean() {
    }


    @Override
    public int compareTo(@NonNull ItemContentBean o) {
        return o.getQuantity()-this.getQuantity();
    }

    public String getProduct() {
        return this.product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public long getOrderId() {
        return this.orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getTotalPrice() {
        return this.totalPrice;
    }
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
    public int getColdHotProperty() {
        return this.coldHotProperty;
    }
    public void setColdHotProperty(int coldHotProperty) {
        this.coldHotProperty = coldHotProperty;
    }
    public String getRecipeFittings() {
        return this.recipeFittings;
    }
    public void setRecipeFittings(String recipeFittings) {
        this.recipeFittings = recipeFittings;
    }
    public int getProduceProcess() {
        return produceProcess;
    }
    public void setProduceProcess(int produceProcess) {
        this.produceProcess = produceProcess;
    }

    @Override
    public String toString() {
        return "ItemContentBean{" +
                "product='" + product + '\'' +
                ", orderId=" + orderId +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", coldHotProperty=" + coldHotProperty +
                ", recipeFittings='" + recipeFittings + '\'' +
                ", produceProcess=" + produceProcess +
                '}';
    }
}
