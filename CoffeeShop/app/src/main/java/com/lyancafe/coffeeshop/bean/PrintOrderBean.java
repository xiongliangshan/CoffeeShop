package com.lyancafe.coffeeshop.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public class PrintOrderBean {

    private long orderId;     //订单号
    private int shopOrderNo;  //门店单号
    private boolean isGiftBox; //是否有礼盒
    private boolean isHaveRemarks; //是否有备注
    private String orderSn;
    private int instant; //1:及时单,2:预约单
    private int boxAmount;    //盒子数量
    private int boxNumber;    //盒号
    private int cupAmount;    //当前盒子中的杯数
    private String receiverName; //收货人的姓名
    private String receiverPhone;  //收货人的电话号码
    private String address;      //收货人的地址
    private long expectedTime;   //期望送达时间
    private List<PrintCupBean> coffeeList;

    public PrintOrderBean() {
    }

    public PrintOrderBean(int boxAmount, int boxNumber, int cupAmount) {
        this.boxAmount = boxAmount;
        this.boxNumber = boxNumber;
        this.cupAmount = cupAmount;
    }

    public PrintOrderBean(long orderId, int shopOrderNo, boolean isGiftBox, boolean isHaveRemarks, String orderSn, int instant, int boxAmount, int boxNumber, int cupAmount, String receiverName, String receiverPhone, String address, long expectedTime, List<PrintCupBean> coffeeList) {
        this.orderId = orderId;
        this.shopOrderNo = shopOrderNo;
        this.isGiftBox = isGiftBox;
        this.isHaveRemarks = isHaveRemarks;
        this.orderSn = orderSn;
        this.instant = instant;
        this.boxAmount = boxAmount;
        this.boxNumber = boxNumber;
        this.cupAmount = cupAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.expectedTime = expectedTime;
        this.coffeeList = coffeeList;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(int shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public boolean isGiftBox() {
        return isGiftBox;
    }

    public void setIsGiftBox(boolean isGiftBox) {
        this.isGiftBox = isGiftBox;
    }

    public boolean isHaveRemarks() {
        return isHaveRemarks;
    }

    public void setIsHaveRemarks(boolean isHaveRemarks) {
        this.isHaveRemarks = isHaveRemarks;
    }

    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
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


    public List<PrintCupBean> getCoffeeList() {
        return coffeeList;
    }

    public long getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    public void setCoffeeList(List<PrintCupBean> coffeeList) {
        this.coffeeList = coffeeList;
    }

    @Override
    public String toString() {
        return "PrintOrderBean{" +
                "orderId=" + orderId +
                ", shopOrderNo=" + shopOrderNo +
                ", isGiftBox=" + isGiftBox +
                ", isHaveRemarks=" + isHaveRemarks +
                ", orderSn='" + orderSn + '\'' +
                ", instant=" + instant +
                ", boxAmount=" + boxAmount +
                ", boxNumber=" + boxNumber +
                ", cupAmount=" + cupAmount +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", address='" + address + '\'' +
                ", expectedTime=" + expectedTime +
                ", coffeeList=" + coffeeList +
                '}';
    }
}
