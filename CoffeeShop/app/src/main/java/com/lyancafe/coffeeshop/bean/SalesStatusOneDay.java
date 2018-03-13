package com.lyancafe.coffeeshop.bean;

/**
 * @author  yangjz on 2018/3/11.
 */

public class SalesStatusOneDay {

    private int totalSales; //销售额
    private int totalCups;  //总杯量
    private int moneyCups;  //付费的杯数
    private int freeCups;   //免费的杯数


    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalCups() {
        return totalCups;
    }

    public void setTotalCups(int totalCups) {
        this.totalCups = totalCups;
    }

    public int getMoneyCups() {
        return moneyCups;
    }

    public void setMoneyCups(int moneyCups) {
        this.moneyCups = moneyCups;
    }

    public int getFreeCups() {
        return freeCups;
    }

    public void setFreeCups(int freeCups) {
        this.freeCups = freeCups;
    }

    @Override
    public String toString() {
        return "SalesStatusOneDay{" +
                "totalSales=" + totalSales +
                ", totalCups=" + totalCups +
                ", moneyCups='" + moneyCups + '\'' +
                ", freeCups='" + freeCups + '\'' +
                '}';
    }

}
