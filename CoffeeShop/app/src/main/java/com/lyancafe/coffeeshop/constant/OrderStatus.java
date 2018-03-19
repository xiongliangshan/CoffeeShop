package com.lyancafe.coffeeshop.constant;

/**
 * Created by Administrator on 2016/7/27.
 */
public class OrderStatus {

    /**
     * 订单的配送状态，针对小哥
     */
    public static final int UNASSIGNED = 3010;    //待抢
    public static final int ASSIGNED = 3020;      //已抢,待取货
    public static final int DELIVERING = 5000;    //配送中
    public static final int FINISHED = 6000;      //已经完成

    /**
     * 订单的生产状态，针对咖啡师
     */
    public static final int UNPRODUCED = 4000;    //待生产
    public static final int PRODUCING = 4005;     //生产中
    public static final int PRODUCED = 4010;      //已生产

    /**
     * 订单异常，针对订单
     */
    public static final int CANCELLED = 6010;  //订单取消
}
