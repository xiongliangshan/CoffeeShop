package com.lyancafe.coffeeshop.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
@Entity
public class ItemContentBean implements Serializable{

    private static final long serialVersionUID = 33565800L;

    @NotNull
    private String product;        //咖啡名称
    private String unit;           //杯型
    private int price;             //单价,单位：分
    private int quantity;          //数量
    private int totalPrice;        //总价,单位：分
    private int coldHotProperty;   //1.冷  2.热  3.常温
    private String recipeFittings; //个性化标签
//    @Transient
//    private List<String> recipeFittingsList; //个性化标签


    public ItemContentBean() {
    }


    @Generated(hash = 792276889)
    public ItemContentBean(@NotNull String product, String unit, int price, int quantity, int totalPrice, int coldHotProperty, String recipeFittings) {
        this.product = product;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.coldHotProperty = coldHotProperty;
        this.recipeFittings = recipeFittings;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getColdHotProperty() {
        return coldHotProperty;
    }

    public void setColdHotProperty(int coldHotProperty) {
        this.coldHotProperty = coldHotProperty;
    }

    public String getRecipeFittings() {
        return recipeFittings;
    }

    public void setRecipeFittings(String recipeFittings) {
        this.recipeFittings = recipeFittings;
    }

    @Override
    public String toString() {
        return "ItemContentBean{" +
                "product='" + product + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", coldHotProperty=" + coldHotProperty +
                ", recipeFittings='" + recipeFittings + '\'' +
                '}';
    }
}
