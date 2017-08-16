package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2016/1/26.
 */
public class PrintCupBean {

    private long orderId;  //此杯咖啡所属的订单id
    private String shopOrderNo;  //门店单号
    private int instant; //1:及时单,2:预约单
    private int boxAmount; //盒子数量
    private int boxNumber; //盒号
    private int cupAmount; //当前盒中的杯量
    private int cupNumber; //杯号
    private String coffee; //咖啡名称
    private String posStr; //编号位置，如 "1-1|2-2",可以作为此杯在本订单中的唯一标识
    private int coldHotProperty;   //1.冷  2.热  3.常温
    private String label;    //个性化


    public PrintCupBean() {
    }

    public PrintCupBean(int boxAmount, int boxNumber, int cupAmount, int cupNumber) {
        this.boxAmount = boxAmount;
        this.boxNumber = boxNumber;
        this.cupAmount = cupAmount;
        this.cupNumber = cupNumber;
        this.posStr = boxAmount+"-"+boxNumber+"|"+cupAmount+"-" +cupNumber;
    }


    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(String shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
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

    public String getPosStr() {
        return posStr;
    }

    public void setPosStr(String posStr) {
        this.posStr = posStr;
    }

    public int getColdHotProperty() {
        return coldHotProperty;
    }

    public void setColdHotProperty(int coldHotProperty) {
        this.coldHotProperty = coldHotProperty;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return "PrintCupBean{" +
                "orderId=" + orderId +
                ", shopOrderNo='" + shopOrderNo + '\'' +
                ", instant=" + instant +
                ", boxAmount=" + boxAmount +
                ", boxNumber=" + boxNumber +
                ", cupAmount=" + cupAmount +
                ", cupNumber=" + cupNumber +
                ", coffee='" + coffee + '\'' +
                ", posStr='" + posStr + '\'' +
                ", coldHotProperty=" + coldHotProperty +
                ", label='" + label + '\'' +
                '}';
    }
}
