package com.lyancafe.coffeeshop.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/9/29.
 */
public class OrderHelper {

    private static final String TAG ="OrderHelper";
    public static String PRINT_STATUS = "print_status";
    public static final int MERGECUPLIMIT = 10; //最大合并杯数限制为10杯
    public static int batchOrderCount = 0;
    public static int batchHandleCupCount = 0;
    public static List<OrderBean> batchList = new ArrayList<>();
    public static Map<String,Integer> contentMap = new HashMap<>();

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
//    public static final int DELIVERY_TIME = 3; //按配送时效

    /**
     * 筛选订单类型
     */
    public static final int APPOINTMENT = 0;    //预约单
    public static final int INSTANT = 1;    //尽快送达
    public static final int ALL = 99;       //全部


    /**
     * 把总价格式化显示
     * @param money 单位为分
     * @return
     */
    public static String getMoneyStr(int money) {
        DecimalFormat df   =new  DecimalFormat("#.##");
        return "￥" + df.format(money/100.0);
    }

    /*
    * converts dip to px
    */
    public static int dip2Px(float dip,Context context) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToMonthDay(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm");
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

    /*时间段毫秒转化为分钟*/
    public static String getDateToMinutes(long time) {
        String min = "";
        String sec = "";
        long minutes = time / (1000 * 60 );
        long seconds = (time % (1000 * 60)) / 1000;
        if(minutes==0){
            min = "00";
        }else{
            min = minutes+"";
        }

        if(seconds<=9){
            sec = "0"+seconds;
        }else{
            sec = seconds+"";
        }

        return  min+":"+sec;
    }

    //计算某个订单的总杯数
    public static int getTotalQutity(OrderBean orderBean){
        if(orderBean.getItems().size()<=0){
            return 0;
        }
        int sum = 0;
        for(int i=0;i<orderBean.getItems().size();i++){
            sum += orderBean.getItems().get(i).getQuantity();
        }
        return sum;
    }
    //计算某个订单的总金额,单位为分
    public static int getTotalPrice(OrderBean orderBean){
        if(orderBean.getItems().size()<=0){
            return 0;
        }
        int sum = 0;
        for(int i=0;i<orderBean.getItems().size();i++){
            ItemContentBean item = orderBean.getItems().get(i);
            sum += item.getTotalPrice();
        }
        return sum;
    }
    //显示时效
    public static void showEffect(OrderBean order,TextView produceBtn,TextView effectTimeTxt){
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);
        if(mms<=0){
            effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
            produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
            effectTimeTxt.setText("+"+OrderHelper.getDateToMinutes(Math.abs(mms)));
        }else{
            if(order.getInstant()==0){
                /*if(Math.abs(mms)-OrderHelper.getTotalQutity(order)*2*60*1000>0){
                    produceBtn.setEnabled(false);
                }else{
                    produceBtn.setEnabled(true);
                }*/
                produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
            }else{
                produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            }
            effectTimeTxt.setTextColor(Color.parseColor("#000000"));
            effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
        }
    }

    //缓存订单打印状态到本地xml文件
    public static void addPrintedSet(Context context,String orderSn){
        Set<String> list = getPrintedSet(context);
        list.add(orderSn);
        SharedPreferences sp = context.getSharedPreferences(PRINT_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor  =sp.edit();
        editor.clear();
        editor.putStringSet("printedOrderList",list);
        editor.commit();
        Log.d(TAG,"order "+orderSn+" is added printSet");
    }

    //获取已打印订单的集合
    public static Set<String> getPrintedSet(Context context){
        SharedPreferences sp = context.getSharedPreferences(PRINT_STATUS, Context.MODE_PRIVATE);
        return sp.getStringSet("printedOrderList",new HashSet<String>());
    }
    //判断订单是否已经被打印过
    public static boolean isPrinted(Context context,String orderSn){
        Set<String> list = getPrintedSet(context);
        for(String order:list){
            Log.d(TAG,"set contain :"+order);
        }
        if(list.contains(orderSn)){
            return true;
        }else{
            return false;
        }
    }
    //清空打印状态缓存记录
    public static void clearPrintedSet(Context context){
        SharedPreferences sp = context.getSharedPreferences(PRINT_STATUS, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        Log.d(TAG,"clear print set");
    }

    //计算应该合并的订单集
    public static void calculateToMergeOrders(List<OrderBean> list){
        batchHandleCupCount = 0;
        batchList.clear();
        contentMap.clear();
        int sum = 0;
        for(OrderBean bean:list){
            //只针对及时单
            if(bean.getInstant()==0){
                continue;
            }
            sum+=getTotalQutity(bean);
            if(sum<=MERGECUPLIMIT){
                batchList.add(bean);
            }else{
                sum-=getTotalQutity(bean);
                break;
            }
        }
        batchHandleCupCount = sum;
        batchOrderCount = batchList.size();
        getBatchMap(batchList);
    }

    //计算订单列表的的咖啡名和对应的杯数
    public static void getBatchMap(List<OrderBean> orderList){
        for(int i=0;i<orderList.size();i++){
            List<ItemContentBean> itemList = orderList.get(i).getItems();
            for(int j=0;j<itemList.size();j++){
                ItemContentBean item = itemList.get(j);
                if(contentMap.containsKey(item.getProduct())){
                    int newCount = contentMap.get(item.getProduct())+item.getQuantity();
                    contentMap.put(item.getProduct(),newCount);
                }else {
                    contentMap.put(item.getProduct(),item.getQuantity());
                }
            }
        }
    }

    //生成合并订单的咖啡内容信息
    public static String createPromptStr(Context context,List<OrderBean> batchList,int cupCount){
        int orderCount = batchList.size();
        List<Map.Entry<String, Integer>> list_map = new ArrayList<>(contentMap.entrySet());
        Collections.sort(list_map, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return rhs.getValue() - lhs.getValue();
            }
        });
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Integer> entry:list_map){
            sb.append(entry.getKey() + " * " + entry.getValue()+"\t");
        }
        return context.getResources().getString(R.string.batch_coffee_prompt,orderCount,cupCount,cupCount*2,sb.toString());
    //    return "系统已将"+orderCount+"单合并在一起，共有"+cupCount+"杯咖啡待生产，生产时效为"+cupCount*2+"分钟\n建议生产顺序为 : "+sb.toString();
    }
}
