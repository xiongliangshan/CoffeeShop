package com.lyancafe.coffeeshop.constant;

/**
 * Created by Administrator on 2016/7/28.
 */
public class OrderAction {

    /**
     * 定义对订单的操作，如开始生产，生产完成，扫码交付
     */

    public static final int STARTPRODUCE = 1001;  //开始生产
    public static final int FINISHPRODUCE = 1002; //生产完成
    public static final int SCANCODE = 1003;      //扫码交付

    public static final int ISSUEORDER = 1004;    //提交问题订单
}
