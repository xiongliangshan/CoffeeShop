package com.lyancafe.coffeeshop.helper;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/29.
 */
public class OrderHelper {

    /**
     * 订单状态
     */
    public static final int DELIVERING_STATUS = 5000;
    public static final int DELIVERED_STATUS = 6000;
    public static final int UNASSIGNED_STATUS = 3010;
    public static final int ASSIGNED_STATUS = 3020;

    /**
     * 排序规则
     */
    public static final int ORDER_TIME = 1;    //按下单时间
    public static final int PRODUCE_TIME = 2;  //按生产时效
    public static final int DELIVERY_TIME = 3; //按配送时效

    /**
     * 筛选订单类型
     */
    public static final int APPOINTMENT = 0;    //预约单
    public static final int INSTANT = 1;    //尽快送达
    public static final int ALL = 99;       //全部












    /*
    * converts dip to px
    */
    public static int dip2Px(float dip,Context context) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try{
            date = sdf.parse(time);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*时间毫秒转化为分钟*/
    public static String getDateToMinutes(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("mm:ss");
        return sf.format(d);
    }
}
