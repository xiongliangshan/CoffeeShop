package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ExceptionalOrder extends OrderBean{

    private int acceptOrPickup;//1,未接  2,未取
    private int acceptTimeOverInt;//海葵接单超时分钟
    private int pickupTimeOverInt;//海葵取单超时分钟
    private String thirdOrderNo;//第三方订单号


    public int getAcceptOrPickup() {
        return acceptOrPickup;
    }

    public void setAcceptOrPickup(int acceptOrPickup) {
        this.acceptOrPickup = acceptOrPickup;
    }

    public int getAcceptTimeOverInt() {
        return acceptTimeOverInt;
    }

    public void setAcceptTimeOverInt(int acceptTimeOverInt) {
        this.acceptTimeOverInt = acceptTimeOverInt;
    }

    public int getPickupTimeOverInt() {
        return pickupTimeOverInt;
    }

    public void setPickupTimeOverInt(int pickupTimeOverInt) {
        this.pickupTimeOverInt = pickupTimeOverInt;
    }

    public String getThirdOrderNo() {
        return thirdOrderNo;
    }

    public void setThirdOrderNo(String thirdOrderNo) {
        this.thirdOrderNo = thirdOrderNo;
    }


    @Override
    public String toString() {

        return super.toString()+"ExceptionalOrder{" +
                "acceptOrPickup=" + acceptOrPickup +
                ", acceptTimeOverInt=" + acceptTimeOverInt +
                ", pickupTimeOverInt=" + pickupTimeOverInt +
                ", thirdOrderNo='" + thirdOrderNo + '\'' +
                '}';
    }
}
