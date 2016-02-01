package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2016/1/26.
 */
public class PrintCupBean {

    private long orderId;  //此杯咖啡所属的订单id
    private int boxNumber; //盒号
    private int boxAmount; //盒子数量
    private int cupNumber; //杯号
    private int cupAmount; //当前盒中的杯量
    private String coffee; //咖啡名称

    public PrintCupBean(long orderId, int boxNumber, int boxAmount, int cupNumber, int cupAmount, String coffee) {
        this.orderId = orderId;
        this.boxNumber = boxNumber;
        this.boxAmount = boxAmount;
        this.cupNumber = cupNumber;
        this.cupAmount = cupAmount;
        this.coffee = coffee;
    }


    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    public int getCupNumber() {
        return cupNumber;
    }

    public void setCupNumber(int cupNumber) {
        this.cupNumber = cupNumber;
    }

    public int getCupAmount() {
        return cupAmount;
    }

    public void setCupAmount(int cupAmount) {
        this.cupAmount = cupAmount;
    }

    public String getCoffee() {
        return coffee;
    }

    public void setCoffee(String coffee) {
        this.coffee = coffee;
    }

    @Override
    public String toString() {
        return "PrintCupBean{" +
                "orderId=" + orderId +
                ", boxNumber=" + boxNumber +
                ", boxAmount=" + boxAmount +
                ", cupNumber=" + cupNumber +
                ", cupAmount=" + cupAmount +
                ", coffee='" + coffee + '\'' +
                '}';
    }
}