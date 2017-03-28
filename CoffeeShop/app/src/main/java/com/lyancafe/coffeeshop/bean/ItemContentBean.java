package com.lyancafe.coffeeshop.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ItemContentBean implements Serializable{

    private String product;        //咖啡名称
    private String unit;           //杯型
    private int price;             //单价,单位：分
    private int quantity;          //数量
    private int totalPrice;        //总价,单位：分
    private int coldHotProperty;   //1.冷  2.热  3.常温
    private List<String> recipeFittingsList; //个性化标签


    public ItemContentBean(String product, String unit, int price, int quantity, int totalPrice, int coldHotProperty, List<String> recipeFittingsList) {
        this.product = product;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.coldHotProperty = coldHotProperty;
        this.recipeFittingsList = recipeFittingsList;
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

    public List<String> getRecipeFittingsList() {
        return recipeFittingsList;
    }

    public void setRecipeFittingsList(List<String> recipeFittingsList) {
        this.recipeFittingsList = recipeFittingsList;
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
                ", recipeFittingsList=" + recipeFittingsList +
                '}';
    }
}
