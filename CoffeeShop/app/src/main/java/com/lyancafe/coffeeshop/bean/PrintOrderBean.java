package com.lyancafe.coffeeshop.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public class PrintOrderBean {

    private long orderId;     //订单号
    private String orderSn;
    private int boxNumber;    //盒号
    private int boxAmount;    //盒子数量
    private int cupAmount;    //当前盒子中的杯数
    private String receiverName; //收货人的姓名
    private String receiverPhone;  //收货人的电话号码
    private String address;      //收货人的地址
    private String deliverName;  //配送员姓名
    private String deliverPhone; //配送员电话
    private List<String> coffeeList;  //当前盒中的咖啡清单

    public PrintOrderBean(long orderId,String orderSn, int boxNumber, int boxAmount, int cupAmount, String receiverName, String receiverPhone, String address, String deliverName, String deliverPhone, List<String> coffeeList) {
        this.orderId = orderId;
        this.orderSn = orderSn;
        this.boxNumber = boxNumber;
        this.boxAmount = boxAmount;
        this.cupAmount = cupAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.deliverName = deliverName;
        this.deliverPhone = deliverPhone;
        this.coffeeList = coffeeList;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public int getBoxAmount() {
        return boxAmount;
    }

    public void setBoxAmount(int boxAmount) {
        this.boxAmount = boxAmount;
    }

    public int getCupAmount() {
        return cupAmount;
    }

    public void setCupAmount(int cupAmount) {
        this.cupAmount = cupAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliverName() {
        return deliverName;
    }

    public void setDeliverName(String deliverName) {
        this.deliverName = deliverName;
    }

    public String getDeliverPhone() {
        return deliverPhone;
    }

    public void setDeliverPhone(String deliverPhone) {
        this.deliverPhone = deliverPhone;
    }

    public List<String> getCoffeeList() {
        return coffeeList;
    }

    public void setCoffeeList(List<String> coffeeList) {
        this.coffeeList = coffeeList;
    }

    @Override
    public String toString() {
        return "PrintOrderBean{" +
                "orderId=" + orderId +
                ", boxNumber=" + boxNumber +
                ", boxAmount=" + boxAmount +
                ", cupAmount=" + cupAmount +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", address='" + address + '\'' +
                ", deliverName='" + deliverName + '\'' +
                ", deliverPhone='" + deliverPhone + '\'' +
                ", coffeeList=" + coffeeList +
                '}';
    }
}
