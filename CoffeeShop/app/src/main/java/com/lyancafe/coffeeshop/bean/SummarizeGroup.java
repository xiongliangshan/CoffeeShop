package com.lyancafe.coffeeshop.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/20.
 */

public class SummarizeGroup {

    private String groupName;   //本组头部信息
    private long expetedTime;  //本组订单对应的期望送达时间
    private List<OrderBean> orders; //本组的所有订单

    private int orderCount;//单量
    private int cupsCount;//杯量
    private int boxCount;//预装盒量
    private List<Map<String,Integer>> deliverTeamList;  //不同配送平台对应的杯量和单量
    private Map<String,Integer> boxOrderMap; // 2盒单，3盒单 等各占多少数量
    private Map<String,Integer> iconMap; //每个图标有少数量
    private Map<String,Integer> cupBoxMap;//单杯盒，两杯盒等各多少数量
    private Map<String,Integer> coffeeMap;//咖啡师制作的咖啡及数量
    private Map<String,Integer> drinkMap; //饮品师制作的咖啡及数量


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getExpetedTime() {
        return expetedTime;
    }

    public void setExpetedTime(long expetedTime) {
        this.expetedTime = expetedTime;
    }

    public List<OrderBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderBean> orders) {
        this.orders = orders;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getCupsCount() {
        return cupsCount;
    }

    public void setCupsCount(int cupsCount) {
        this.cupsCount = cupsCount;
    }

    public int getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(int boxCount) {
        this.boxCount = boxCount;
    }

    public List<Map<String, Integer>> getDeliverTeamList() {
        return deliverTeamList;
    }

    public void setDeliverTeamList(List<Map<String, Integer>> deliverTeamList) {
        this.deliverTeamList = deliverTeamList;
    }

    public Map<String, Integer> getBoxOrderMap() {
        return boxOrderMap;
    }

    public void setBoxOrderMap(Map<String, Integer> boxOrderMap) {
        this.boxOrderMap = boxOrderMap;
    }

    public Map<String, Integer> getIconMap() {
        return iconMap;
    }

    public void setIconMap(Map<String, Integer> iconMap) {
        this.iconMap = iconMap;
    }

    public Map<String, Integer> getCupBoxMap() {
        return cupBoxMap;
    }

    public void setCupBoxMap(Map<String, Integer> cupBoxMap) {
        this.cupBoxMap = cupBoxMap;
    }

    public Map<String, Integer> getCoffeeMap() {
        return coffeeMap;
    }

    public void setCoffeeMap(Map<String, Integer> coffeeMap) {
        this.coffeeMap = coffeeMap;
    }

    public Map<String, Integer> getDrinkMap() {
        return drinkMap;
    }

    public void setDrinkMap(Map<String, Integer> drinkMap) {
        this.drinkMap = drinkMap;
    }

    @Override
    public String toString() {
        return "SummarizeGroup{" +
                "groupName='" + groupName + '\'' +
                ", expetedTime=" + expetedTime +
                ", orders=" + orders.size() +
                ", orderCount=" + orderCount +
                ", cupsCount=" + cupsCount +
                ", boxCount=" + boxCount +
                ", deliverTeamList=" + deliverTeamList +
                ", boxOrderMap=" + boxOrderMap +
                ", iconMap=" + iconMap +
                ", cupBoxMap=" + cupBoxMap +
                ", coffeeMap=" + coffeeMap +
                ", drinkMap=" + drinkMap +
                '}';
    }
}
