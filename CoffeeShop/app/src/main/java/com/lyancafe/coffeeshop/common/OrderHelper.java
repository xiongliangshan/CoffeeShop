package com.lyancafe.coffeeshop.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.DeliverPlatform;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.bean.Product;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/9/29.
 */
public class OrderHelper {

    private static final String TAG ="OrderHelper";
    public static String PRINT_STATUS = "print_status";
    public static final int MERGECUPLIMIT = 10; //最大合并杯数限制为10杯
    public static final int ADVANCEDHANDLETIME = 45;//预约单可提前操作的时间,单位：分钟
    public static int batchOrderCount = 0;
    public static int batchHandleCupCount = 0;
    public static List<OrderBean> batchList = new ArrayList<>();
    public static Map<String,Integer> contentMap = new HashMap<>();

    public static long DELAY_LOAD_TIME = 200;  //单位 ms


    /**
     * 把总价格式化显示
     * @param money 单位为分
     * @return
     */
    public static String getMoneyStr(int money) {
        DecimalFormat df   =new  DecimalFormat("#.##");
        return "￥" + df.format(money/100.0);
    }

    //距离显示格式化
    public static String getDistanceFormat(int distance){
        DecimalFormat df   =new  DecimalFormat("#.#");
        return df.format(distance/1000.0)+"公里";
    }

    //距离显示格式化
    public static String getDistanceFormat(double distance){
        DecimalFormat df   =new  DecimalFormat("#.#");
        if(distance>=1000){
            return df.format(distance/1000.0)+" 公里";
        }else{
            return new DecimalFormat("#").format(distance)+" 米";
        }

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
        if(time == 0){
            return "-- --";
        }
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm",Locale.CHINESE);
        return sf.format(d);
    }

    public static String getFormatTimeToStr(long time) {
        if(time == 0){
            return "-- --";
        }
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToMonthDay(long time) {
        if(time == 0){
            return "-- --";
        }
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
        long t2 = time+30*60*1000;
        Date d = new Date(time);
        return new SimpleDateFormat("MM-dd",Locale.CHINESE).format(d)+" "+sf.format(d)+"~"+sf.format(new Date(t2));
    }

    /*时间戳转换成字符窜*/
    public static String getFormatPeriodTimeStr(long time) {
        if(time == 0){
            return "-- --";
        }
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
        long t2 = time+30*60*1000;
        Date d = new Date(time);
        return sf.format(d)+"~"+sf.format(new Date(t2));
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);
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
    private static String getDateToMinutes(long time) {
        String min = "";
//        String sec = "";
        long minutes = time / (1000 * 60 );
//        long seconds = (time % (1000 * 60)) / 1000;
        if(minutes==0){
            min = "00";
        }else{
            min = minutes+"";
        }

//        if(seconds<=9){
//            sec = "0"+seconds;
//        }else{
//            sec = seconds+"";
//        }

        return  min;
    }


    //计算某个订单的总杯数
    public static int getTotalQutity(OrderBean orderBean){
        if(orderBean.getItems()==null || orderBean.getItems().size()<=0){
            return 0;
        }
        int sum = 0;
        for(int i=0;i<orderBean.getItems().size();i++){
            sum += orderBean.getItems().get(i).getQuantity();
        }
        return sum;
    }

    //计算某个订单集合的总杯数
    public static int getTotalQutity(List<OrderBean> orders){
        if(orders==null || orders.size()==0){
            return 0;
        }
        int sum = 0;
        for(int i = 0;i<orders.size();i++){
            sum+=getTotalQutity(orders.get(i));
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
    //显示时效并修改生产完成的按钮状态
    public static void showEffect(OrderBean order,TextView produceBtn,TextView effectTimeTxt){
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);
        if(mms<=0){
            effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
            if(produceBtn!=null){
                produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
            }
            effectTimeTxt.setText(String.format("+%s", OrderHelper.getDateToMinutes(Math.abs(mms))));
        }else{
            if(order.getInstant()==0){
                if(produceBtn!=null){
                    produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                }
            }else{
                if(produceBtn!=null){
                    produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                }
            }
            effectTimeTxt.setTextColor(Color.parseColor("#000000"));
            effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
        }
    }

    //单独显示待生产界面的时效
    public static void showEffectOnly(OrderBean order,TextView effectTimeTxt){
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);
        if(mms<=0){
            effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
            effectTimeTxt.setText(String.format("超%s", OrderHelper.getDateToMinutes(Math.abs(mms))));
        }else{
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
        editor.apply();
        Log.d(TAG,"order "+orderSn+" is added printSet");
    }

    //获取已打印订单的集合
    private static Set<String> getPrintedSet(Context context){
        SharedPreferences sp = context.getSharedPreferences(PRINT_STATUS, Context.MODE_PRIVATE);
        return sp.getStringSet("printedOrderList",new HashSet<String>());
    }
    //判断订单是否已经被打印过
    public static boolean isPrinted(Context context,String orderSn){
        Set<String> list = getPrintedSet(context);
        return list.contains(orderSn);
    }
    //筛选出未打印的订单集合
    public static List<OrderBean> selectUnPrintList(Context context,List<OrderBean> list){
        List<OrderBean> unPrintList = new ArrayList<>();
        for(OrderBean orderBean:list){
            if(!OrderHelper.isPrinted(context,orderBean.getOrderSn())){
                unPrintList.add(orderBean);
            }
        }
        return unPrintList;
    }

    //清空打印状态缓存记录
    public static void clearPrintedSet(Context context){
        SharedPreferences sp = context.getSharedPreferences(PRINT_STATUS, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        Log.d(TAG, "clear print set");
    }

    //计算应该合并的订单集
    public static void calculateToMergeOrders(List<OrderBean> list){
        batchHandleCupCount = 0;
        batchList.clear();
        contentMap.clear();
        int sum = 0;
        for(OrderBean bean:list){
            //非待取货订单不加入合并
            if(bean.getStatus()!= OrderStatus.ASSIGNED){
                continue;
            }
            //如果没达到能操作条件的预约单不加入合并
            if(bean.getInstant()==0){
                if(!isCanHandle(bean)){
                    continue;
                }
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
    private static void getBatchMap(List<OrderBean> orderList){
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
            sb.append(entry.getKey() + " * " + entry.getValue()+"  ");
        }
        return context.getResources().getString(R.string.batch_coffee_prompt,orderCount,cupCount,cupCount*2,sb.toString());
    //    return "系统已将"+orderCount+"单合并在一起，共有"+cupCount+"杯咖啡待生产，生产时效为"+cupCount*2+"分钟\n建议生产顺序为 : "+sb.toString();
    }


    //判断一个订单是否已经处于批量处理中
    public static boolean isBatchOrder(long orderId){
        for(OrderBean orderBean:batchList){
            if(orderBean.getId()==orderId){
                return true;
            }
        }
        return false;
    }

    //从批量处理的订单集合中移除某个订单,返回当前的批量订单数量
    public static int removeOrderFromBatchList(long orderId){
        if(batchList.size()<1){
            return 0;
        }
        for(int i=batchList.size()-1;i>=0;i--){
            if(batchList.get(i).getId()==orderId){
                batchList.remove(i);
                break;
            }
        }
        return batchList.size();
    }

    //判断预约单是否可以进行打印生产操作
    public static boolean isCanHandle(OrderBean order){
        if(order.getInstant()!=0){
            return true;
        }
        int totalQutity = getTotalQutity(order);
        if(totalQutity<=5){
            final long mms = System.currentTimeMillis() - (order.getExpectedTime() - ADVANCEDHANDLETIME * 60 * 1000);
            return mms>=0;
        }else{
            final long mms = System.currentTimeMillis() - (order.getExpectedTime() - (totalQutity*2+ADVANCEDHANDLETIME) * 60 * 1000);
            return mms>=0;
        }

    }

    //检查新订单集合中是否含有批处理订单
    public static boolean isContainerBatchOrder(List<OrderBean> list){
        for(OrderBean batchorder:batchList){
            for(OrderBean order:list){
                if(order.getId()==batchorder.getId()){
                    return true;
                }
            }
        }
        return false;
    }

    //多个评论标签组合成一个特定字符串
    public static String getCommentTagsStr(List<String> tags){
        StringBuilder sb = new StringBuilder();
        for(String tag:tags){
            sb.append("[");
            sb.append(tag);
            sb.append("]");
        }
        return sb.toString();
    }


    public static String getShopOrderSn(OrderBean orderBean){
        if(orderBean.getDeliveryTeam()== DeliveryTeam.MEITUAN){
            return "美团"+orderBean.getThirdShopOrderNo();
        }
        if(orderBean.getDeliveryTeam()== DeliveryTeam.ELE){
            return "饿"+orderBean.getThirdShopOrderNo();
        }
        if(orderBean.getInstant()==1){//及时单
            return String.valueOf(orderBean.getShopOrderNo());
        }else{
            return orderBean.getShopOrderNo()+"约";
        }
    }




   public static String getWxScanStrForPrint(PrintOrderBean printOrderBean){
       return printOrderBean.isWxScan()?"(到店扫)":"";
   }

    public static String getLabelPrintStr(List<String> list){
        if(list==null || list.size()<=0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String label:list){
            sb.append(label);
            sb.append("/");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


    //手机号码隐藏中间四位
    public static String getHidePhone(OrderBean orderBean){
        if(orderBean.getDeliveryTeam()==DeliveryTeam.MEITUAN){
            return "";
        }
        String phone = orderBean.getPhone();
        if(TextUtils.isEmpty(phone) || phone.length()<11){
            return phone;
        }

        return phone.substring(0,phone.length()-8)+"####"+phone.substring(phone.length()-4,phone.length());
    }



    //期望送达时间段
    public static String getPeriodOfExpectedtime(PrintOrderBean pob){
        if(DeliveryTeam.MEITUAN==pob.getDeliveryTeam()){
            //美团订单
            if(pob.getInstant()==1){
                //及时单
                return "立即送出";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
                String start = sdf.format(new Date(pob.getExpectedTime()));
                return start;
            }
        }else{
            if(pob.getInstant()==1){
                //及时单
                return "尽快送达";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
                String start = sdf.format(new Date(pob.getExpectedTime()));
                String end = sdf.format(new Date(pob.getExpectedTime()+30*60*1000));
                return start+"～"+end;
            }
        }

    }

    public static String getPeriodOfExpectedtime(OrderBean orderBean){
        if(DeliveryTeam.MEITUAN==orderBean.getDeliveryTeam()){
            //美团订单
            if(orderBean.getInstant()==1){
                //及时单
                return "立即送出";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
                String start = sdf.format(new Date(orderBean.getExpectedTime()));
                return start;
            }
        }else{
            if(orderBean.getInstant()==1){
                //及时单
                return "尽快送达";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
                String start = sdf.format(new Date(orderBean.getExpectedTime()));
                String end = sdf.format(new Date(orderBean.getExpectedTime()+30*60*1000));
                return start+"~"+end;
            }
        }

    }

    //针对当前时间送达计算订单的服务时效
    public static String getTimeToService(OrderBean orderBean){
        SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm",Locale.CHINESE);
        String result = "";
        if(orderBean.getInstant()==1){  //及时单
            long orderTime = orderBean.getOrderTime();
            long nowTime = System.currentTimeMillis();
            long deltaTime = nowTime - orderTime;
            if(deltaTime<30*60*1000){
                result = "截止"+sdf.format(new Date(orderTime+30*60*1000))+"送达 良好";
            }else if(deltaTime>=30*60*1000 && deltaTime<60*60*1000){
                result = "截止"+sdf.format(new Date(orderTime+60*60*1000))+"送达 合格";
            }else if(deltaTime>=60*60*1000){
                result = "已超时";
            }
        }else{
            long bookTime = orderBean.getExpectedTime();
            long T2 = bookTime+30*60*1000;
            long nowTime = System.currentTimeMillis();
            if(nowTime<T2){
                result = "截止"+sdf.format(new Date(T2))+"送达 良好";
            }else if(nowTime>=T2 && nowTime<=T2+5*60*1000){
                result = "截止"+sdf.format(new Date(T2+5*60*1000))+"送达 合格";
            }else if(nowTime>T2+5*60*1000){
                result = "已超时";
            }
        }
        return result;
    }

    /**
     * 实际该订单的服务时效
     */
    public static String getRealTimeToService(OrderBean orderBean){
        long realReachTime = orderBean.getHandoverTime();
        String result = "";
        if(orderBean.getInstant()==1){  //及时单
            long orderTime = orderBean.getOrderTime();
            long deltaTime = realReachTime - orderTime;
            if(deltaTime<=30*60*1000){
                result = "良好";
            }else if(deltaTime>30*60*1000 && deltaTime<=60*60*1000){
                result = "合格";
            }else if(deltaTime>60*60*1000){
                result = "不及格";
            }
        }else{
            long bookTime = orderBean.getExpectedTime();
            long T2 = bookTime+30*60*1000;
            if(realReachTime>(bookTime-45*60*1000) && realReachTime<=T2){
                result = "良好";
            }else if(realReachTime>T2 && realReachTime<=T2+5*60*1000){
                result = "合格";
            }else if(realReachTime>T2+5*60*1000 || realReachTime<=(bookTime-45*60*1000)){
                result = "不及格";
            }
        }
        return result;
    }



    /**
     * 格式化时间
     */
    public static String formatOrderDate(long time){
        if(time==0){
            return "-- -- -- --";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm (MM/dd)");
        return sdf.format(new Date(time));
    }





    public static String getStatusName(int status,boolean isWxScan) {
        if(isWxScan){
            return "无需配送";
        }
        String statusName;
        switch (status){
            case 2010:
                statusName = "已接单";
                break;
            case 3010:
                statusName = "未指派骑手";
                break;
            case 3020:
                statusName = "未取货";
                break;
            case 5000:
                statusName = "派送中";
                break;
            case 6000:
                statusName = "送达";
                break;
            default:
                statusName = "未知("+status+")";
        }

        return statusName;
    }

    public static String getDeliverTeamName(int deliverTeam){
        String teamName;
        switch (deliverTeam) {
            case DeliveryTeam.LYAN:
                teamName = "自有";
                break;
            case DeliveryTeam.HAIKUI:
                teamName = "海葵";
                break;
            case DeliveryTeam.MEITUAN:
                teamName = "美团";
                break;
            case DeliveryTeam.ELE:
                teamName = "饿了么";
                break;
            default:
                teamName = "未知（" + deliverTeam + ")";
        }
        return teamName;
    }

    public static boolean isTomorrowOrder(OrderBean orderBean){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day_current = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date(orderBean.getExpectedTime()));
        int day_order = calendar1.get(Calendar.DAY_OF_MONTH);


        return day_order > day_current;
    }

    public static String getPrintFlag(String orderSn) {
        return isPrinted(CSApplication.getInstance(),orderSn)?"重复打印":"";
    }

    public static void getIdsFromOrders(List<OrderBean> selectedList,List<Long> orderIds,List<Long> scanIds) {
        for(OrderBean order:selectedList){
            if(order.getWxScan()){
                scanIds.add(order.getId());
            }else{
                orderIds.add(order.getId());
            }

        }
    }

    public static List<Long> getIdsFromOrders(List<OrderBean> selectedList){
        List<Long> orderIds = new ArrayList<>();
        for(OrderBean order:selectedList){
            orderIds.add(order.getId());
        }
        return orderIds;
    }


    ////把所有订单按汇总组来划分
    public static List<SummarizeGroup> splitOrdersToGroup(List<OrderBean> orders){
        List<SummarizeGroup> groups = new ArrayList<>();
        Map<Long,Object> map = new HashMap<>();
        SummarizeGroup jishisg = null;
        for(OrderBean order:orders){
            if(order.getInstant()==1){
                //及时单
                if(jishisg==null){
                    jishisg = new SummarizeGroup();
                    jishisg.setExpetedTime(order.getExpectedTime());
                    jishisg.setGroupName("及时单");
                    jishisg.setOrders(new ArrayList<OrderBean>());
                    jishisg.getOrders().add(order);
                    groups.add(jishisg);
                }else {
                    jishisg.getOrders().add(order);
                }

            }else{
                //预约单
                Object object = map.get(order.getExpectedTime());
                if(object==null){
                    SummarizeGroup sg =  new SummarizeGroup();
                    sg.setExpetedTime(order.getExpectedTime());
                    sg.setOrders(new ArrayList<OrderBean>());
                    sg.setGroupName(getFormatPeriodTimeStr(sg.getExpetedTime()));
                    sg.getOrders().add(order);
                    map.put(order.getExpectedTime(),sg);
                    groups.add(sg);
                }else{
                    SummarizeGroup sg = (SummarizeGroup) object;
                    sg.getOrders().add(order);
                }
            }
        }

        return groups;
    }


    public static List<SummarizeGroup> caculateGroupList(List<SummarizeGroup> groups){
        SummarizeGroup total = new SummarizeGroup();
        total.setGroupName("合计");
        total.setExpetedTime(0L);
        total.setOrders(new ArrayList<OrderBean>());
        total.setDeliverPlatformMap(new HashMap<String, DeliverPlatform>());
        total.setBoxOrderMap(new TreeMap<String, Integer>());
        total.setIconMap(new HashMap<String, Integer>());
        total.setCupBoxMap(new TreeMap<String, Integer>());
        total.setCoffee(new HashMap<String, Product>());
        total.setDrink(new HashMap<String, Product>());
        for(int i=0;i<groups.size();i++){
            SummarizeGroup summarizeGroup = groups.get(i);
            summarizeGroup.setOrderCount(summarizeGroup.getOrders().size());
            caculateGroup(summarizeGroup);

            //计算合计
            total.getOrders().addAll(summarizeGroup.getOrders());
            total.setOrderCount(total.getOrderCount()+summarizeGroup.getOrderCount());
            total.setCupsCount(total.getCupsCount()+summarizeGroup.getCupsCount());
            total.setBoxCount(total.getBoxCount()+summarizeGroup.getBoxCount());
            mergeDeliverPlatform(total.getDeliverPlatformMap(),summarizeGroup.getDeliverPlatformMap());
            mergeMap(total.getBoxOrderMap(),summarizeGroup.getBoxOrderMap());
            mergeMap(total.getIconMap(),summarizeGroup.getIconMap());
            mergeMap(total.getCupBoxMap(),summarizeGroup.getCupBoxMap());
            mergeProduct(total.getCoffee(),summarizeGroup.getCoffee());
            mergeProduct(total.getDrink(),summarizeGroup.getDrink());
        }

        Collections.sort(groups, new Comparator<SummarizeGroup>() {
            @Override
            public int compare(SummarizeGroup o1, SummarizeGroup o2) {
                return (int) (o1.getExpetedTime()-o2.getExpetedTime());
            }
        });
        groups.add(total);

        for(SummarizeGroup group:groups){
            LogUtil.d(TAG,group.toString());
        }

        return groups;
    }

    //两个Map<String,Product>合并,同品类数量相加
    private static void mergeProduct(Map<String,Product> container,Map<String,Product> child){
        Iterator<String> it = child.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            if(container.containsKey(key)){
                Product product = container.get(key);
                product.setCount(product.getCount()+child.get(key).getCount());
            }else{
                container.put(key,child.get(key));
            }
        }
    }

    //两个Map<String,DeliverPlatform>合并，单量杯量分别相加
    private static void mergeDeliverPlatform(Map<String,DeliverPlatform> container,Map<String,DeliverPlatform> child){
        Iterator<String> it = child.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            if(container.containsKey(key)){
                DeliverPlatform dp = container.get(key);
                dp.setOrderCount(dp.getOrderCount()+child.get(key).getOrderCount());
                dp.setCupCount(dp.getCupCount()+child.get(key).getCupCount());
            }else{
                DeliverPlatform dp = child.get(key);
                DeliverPlatform newDp = new DeliverPlatform(dp.getName());
                newDp.setOrderCount(dp.getOrderCount());
                newDp.setCupCount(dp.getCupCount());
                container.put(key, newDp);
            }
        }
    }

    //两个Map<String,Integer> key相同的值相加
    private static void mergeMap(Map<String,Integer> container,Map<String,Integer> child){
        Iterator<String> it = child.keySet().iterator();
        while (it.hasNext()){
            String key =it.next();
            if(container.containsKey(key)){
                container.put(key,container.get(key)+child.get(key));
            }else{
                container.put(key,child.get(key));
            }
        }
    }

    private static void caculateGroup(SummarizeGroup summarizeGroup) {

        summarizeGroup.setIconMap(new HashMap<String, Integer>());
        summarizeGroup.setBoxOrderMap(new TreeMap<String, Integer>());
        summarizeGroup.setCupBoxMap(new TreeMap<String, Integer>());
        summarizeGroup.getCupBoxMap().put("1beihe",0);
        summarizeGroup.getCupBoxMap().put("2beihe",0);
        summarizeGroup.getCupBoxMap().put("4beihe",0);
        int totalCups = 0;
        Map<String,DeliverPlatform> dpMap = new HashMap<>();
        dpMap.put("weifuwu",new DeliverPlatform("微服务"));
        dpMap.put("meituan",new DeliverPlatform("美团"));
        dpMap.put("eleme",new DeliverPlatform("饿了么"));
        dpMap.put("daodiansao",new DeliverPlatform("到店扫"));
        Map<String,Product> mapItem = new HashMap<>();
        List<OrderBean> orders =  summarizeGroup.getOrders();
        for(OrderBean order:orders){
            if("Y".equals(order.getReminder())){
                //催单
                Integer iconNum = summarizeGroup.getIconMap().get("ji");
                if(iconNum==null){
                    summarizeGroup.getIconMap().put("ji", 1);
                }else{
                    summarizeGroup.getIconMap().put("ji",iconNum+1);
                }

            }

            if(order.getCheckAddress()){
                //重点关注地址（眼睛）
                Integer iconNum = summarizeGroup.getIconMap().get("yan");
                if(iconNum==null){
                    summarizeGroup.getIconMap().put("yan", 1);
                }else{
                    summarizeGroup.getIconMap().put("yan",iconNum+1);
                }

            }

            if(!TextUtils.isEmpty(order.getNotes()) || !TextUtils.isEmpty(order.getCsrNotes())){
                //有备注
                Integer iconNum = summarizeGroup.getIconMap().get("bei");
                if(iconNum==null){
                    summarizeGroup.getIconMap().put("bei", 1);
                }else{
                    summarizeGroup.getIconMap().put("bei",iconNum+1);
                }
            }

            if(order.getRelationOrderId()!=0){
                //有补单
                Integer iconNum = summarizeGroup.getIconMap().get("bu");
                if(iconNum==null){
                    summarizeGroup.getIconMap().put("bu", 1);
                }else{
                    summarizeGroup.getIconMap().put("bu",iconNum+1);
                }
            }

            //计算盒单
            int boxCount = caculateBoxOrder(order);
            Integer orderCount = summarizeGroup.getBoxOrderMap().get(boxCount+"hedan");
            if(orderCount==null){
                summarizeGroup.getBoxOrderMap().put(boxCount+"hedan",1);
            }else{
                summarizeGroup.getBoxOrderMap().put(boxCount+"hedan",summarizeGroup.getBoxOrderMap().get(boxCount+"hedan")+1);
            }

            //计算杯盒
            Map<String,Integer> map = caculateCupBox(order);
            summarizeGroup.getCupBoxMap().put("1beihe",summarizeGroup.getCupBoxMap().get("1beihe")+map.get("1beihe"));
            summarizeGroup.getCupBoxMap().put("2beihe",summarizeGroup.getCupBoxMap().get("2beihe")+map.get("2beihe"));
            summarizeGroup.getCupBoxMap().put("4beihe",summarizeGroup.getCupBoxMap().get("4beihe")+map.get("4beihe"));

            //计算总杯量
            totalCups+=getTotalQutity(order);



            if(order.getWxScan()){
                dpMap.get("daodiansao").setOrderCount(dpMap.get("daodiansao").getOrderCount()+1);
                dpMap.get("daodiansao").setCupCount(dpMap.get("daodiansao").getCupCount()+getTotalQutity(order));
            }else {
                if(DeliveryTeam.LYAN==order.getDeliveryTeam() || DeliveryTeam.HAIKUI==order.getDeliveryTeam()){
                    //微服务
                    dpMap.get("weifuwu").setOrderCount(dpMap.get("weifuwu").getOrderCount()+1);
                    dpMap.get("weifuwu").setCupCount(dpMap.get("weifuwu").getCupCount()+getTotalQutity(order));
                }else if(DeliveryTeam.MEITUAN==order.getDeliveryTeam()){
                    //美团
                    dpMap.get("meituan").setOrderCount(dpMap.get("meituan").getOrderCount()+1);
                    dpMap.get("meituan").setCupCount(dpMap.get("meituan").getCupCount()+getTotalQutity(order));
                }else if(DeliveryTeam.ELE==order.getDeliveryTeam()){
                    //饿了么
                    dpMap.get("eleme").setOrderCount(dpMap.get("eleme").getOrderCount()+1);
                    dpMap.get("eleme").setCupCount(dpMap.get("eleme").getCupCount()+getTotalQutity(order));
                }
            }


            //计算咖啡品类数据
            caculateItem(order,mapItem);


        }
        summarizeGroup.setBoxCount(summarizeGroup.getCupBoxMap().get("1beihe")+summarizeGroup.getCupBoxMap().get("2beihe")+
                summarizeGroup.getCupBoxMap().get("4beihe"));
        summarizeGroup.setCupsCount(totalCups);

        summarizeGroup.setDeliverPlatformMap(dpMap);


        Map<String,Product> front = new TreeMap<>();
        Map<String,Product> back = new TreeMap<>();

        Iterator<String> iterator = mapItem.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            Product product = mapItem.get(key);
            if(product.getProduceProcess()==1 || product.getProduceProcess()==3){
                //需要咖啡师全生产或者生产一部分的
                front.put(key,product);
            }else{
                back.put(key,product);
            }
        }

        summarizeGroup.setCoffee(front);
        summarizeGroup.setDrink(back);

    }



    //计算一个单子能预装几盒
    public static int caculateBoxOrder(OrderBean order){
        List<ItemContentBean> items = order.getItems();
        int cold =0;
        int hot = 0;
        int coldBox=0;
        int hotBox=0;
        for(ItemContentBean item:items){
            if(item.getColdHotProperty()==1){
                //冷
                cold+=item.getQuantity();
            }else if(item.getColdHotProperty()==2){
                //热
                hot+=item.getQuantity();
            }
        }

        coldBox = cold>0?(cold>=4?(cold/4+cold%4):1):0;
        hotBox = hot>0?(hot>=4?(hot/4+hot%4):1):0;

        return coldBox+hotBox;
    }

    public static Map<String,Integer> caculateCupBox(OrderBean order){
        Map<String,Integer> map = new HashMap<>();
        map.put("1beihe",0);
        map.put("2beihe",0);
        map.put("4beihe",0);
        List<ItemContentBean> items = order.getItems();
        int cold =0;
        int hot = 0;
        for(ItemContentBean item:items){
            if(item.getColdHotProperty()==1){
                //冷
                cold+=item.getQuantity();
            }else if(item.getColdHotProperty()==2){
                //热
                hot+=item.getQuantity();
            }
        }

        if(cold==1){
            map.put("1beihe",map.get("1beihe")+1);
        }else if(cold==2){
            map.put("2beihe",map.get("2beihe")+1);
        }else if(cold==3){
            map.put("4beihe",map.get("4beihe")+1);
        }else if(cold>=4){
            int a = cold%4;
            if(a==0){
                map.put("4beihe",map.get("4beihe")+cold/4);
            }else {
                map.put("4beihe",map.get("4beihe")+cold/4);
                if(a==3){
                    map.put("4beihe",map.get("4beihe")+1);
                }else {
                    map.put(a+"beihe",map.get(a+"beihe")+1);
                }

            }
        }
        if(hot==1){
            map.put("1beihe",map.get("1beihe")+1);
        }else if(hot==2){
            map.put("2beihe",map.get("2beihe")+1);
        }else if(hot==3){
            map.put("4beihe",map.get("4beihe")+1);
        }else if(hot>=4){
            int a = hot%4;
            if(a==0){
                map.put("4beihe",map.get("4beihe")+hot/4);
            }else {
                map.put("4beihe",map.get("4beihe")+hot/4);
                if(a==3){
                    map.put("4beihe",map.get("4beihe")+1);
                }else {
                    map.put(a+"beihe",map.get(a+"beihe")+1);
                }

            }
        }


        return map;
    }

    public static void caculateItem(OrderBean order,Map<String,Product> map){
        List<ItemContentBean> items = order.getItems();
        if(items==null){
            return ;
        }
        for(ItemContentBean item:items){
            String name = item.getProduct();
            Product product = map.get(name);
            if(product==null){
                product = new Product();
                product.setName(name);
                product.setProduceProcess(item.getProduceProcess());
                product.setCount(item.getQuantity());
                if(!TextUtils.isEmpty(item.getRecipeFittings())){
                    //有口味定制
                    product.setCustom(true);
                }else{
                    product.setCustom(false);
                }
                map.put(name,product);
            }else{
                product.setCount(product.getCount()+item.getQuantity());
            }

        }
    }



    public static Spanned getBoxCupByOrder(OrderBean order){
        int boxCount = caculateBoxOrder(order);
        int cupCount = getTotalQutity(order);
        String htmlStr = "<font color = '#9B9B9B'>总盒</font><font color ='#000000'>"+boxCount+"," +
                "</font><font color = '#9B9B9B'>总杯</font><font color ='#000000'>"+cupCount+"</font>";
        return Html.fromHtml(htmlStr);
    }


    /**
     * 骑手接单相对于现在过了几分钟
     * @param acceptTime
     * @return
     */
    public static String getAcceptOverTime(long acceptTime){
        long delta = System.currentTimeMillis()-acceptTime;
        long minute = delta/(60*1000);
        return minute+"分钟";
    }

    public static String getToArriveTime(long expectedTime){
        long delta = expectedTime + 45*60*1000 - System.currentTimeMillis();
        if(delta<0){
            return "超时"+Math.abs(delta)/(60*1000)+"分钟";
        }else {
            return delta/(60*1000)+"分钟";
        }
    }


}
