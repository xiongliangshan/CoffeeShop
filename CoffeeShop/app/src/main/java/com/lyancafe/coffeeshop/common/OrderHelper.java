package com.lyancafe.coffeeshop.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderStatus;

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
    public static final int ADVANCEDHANDLETIME = 45;//预约单可提前操作的时间,单位：分钟
    public static int batchOrderCount = 0;
    public static int batchHandleCupCount = 0;
    public static List<OrderBean> batchList = new ArrayList<>();
    public static Map<String,Integer> contentMap = new HashMap<>();

    public static long DELAY_LOAD_TIME = 400;  //单位 ms

    public static final int GOOD_COMMENT = 4;  //好评
    public static final int BAD_COMMENT = 5;   //差评


    /**
     * 排序规则
     */
    public static final int ORDER_TIME = 1;    //按下单时间
    public static final int PRODUCE_TIME = 2;  //按生产时效

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
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToMonthDay(long time) {
        if(time == 0){
            return "-- --";
        }
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        long t2 = time+30*60*1000;
        Date d = new Date(time);
        return new SimpleDateFormat("MM-dd").format(d)+" "+sf.format(d)+"~"+sf.format(new Date(t2));
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
    //显示时效并修改生产完成的按钮状态
    public static void showEffect(OrderBean order,TextView produceBtn,TextView effectTimeTxt){
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);
        if(mms<=0){
            effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
            if(produceBtn!=null){
                produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
            }
            effectTimeTxt.setText("+"+OrderHelper.getDateToMinutes(Math.abs(mms)));
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
            effectTimeTxt.setText("+"+OrderHelper.getDateToMinutes(Math.abs(mms)));
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
        sp.edit().clear().commit();
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
            return mms<0?false:true;
        }else{
            final long mms = System.currentTimeMillis() - (order.getExpectedTime() - (totalQutity*2+ADVANCEDHANDLETIME) * 60 * 1000);
            return mms<0?false:true;
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

    /**
     * 打印标签上的门店单号
     * @param orderBean
     * @return
     */
    public static String getPrintShopOrderSn(OrderBean orderBean){
        if(orderBean.getDeliveryTeam()== DeliveryTeam.MEITUAN){
            return "美团"+orderBean.getMtShopOrderNo();
        }
        String shopOrderSn = "";
        if(orderBean.getShopOrderNo()<10){
            shopOrderSn = "00"+orderBean.getShopOrderNo();
        }else if(orderBean.getShopOrderNo()>=10&&orderBean.getShopOrderNo()<100){
            shopOrderSn = "0"+orderBean.getShopOrderNo();
        }else{
            shopOrderSn = ""+orderBean.getShopOrderNo();
        }
        if(orderBean.getInstant()==1){//及时单
            return orderBean.getDeliveryTeam()== DeliveryTeam.HAIKUI?"海葵"+shopOrderSn:shopOrderSn;
        }else{
            return orderBean.getDeliveryTeam()== DeliveryTeam.HAIKUI?"海葵"+shopOrderSn+"约":shopOrderSn+"约";
        }
    }

    public static String getShopOrderSn(OrderBean orderBean){
        if(orderBean.getDeliveryTeam()== DeliveryTeam.MEITUAN){
            return "美团"+orderBean.getMtShopOrderNo();
        }
        String shopOrderSn = "";
        if(orderBean.getShopOrderNo()<10){
            shopOrderSn = "00"+orderBean.getShopOrderNo();
        }else if(orderBean.getShopOrderNo()>=10&&orderBean.getShopOrderNo()<100){
            shopOrderSn = "0"+orderBean.getShopOrderNo();
        }else{
            shopOrderSn = ""+orderBean.getShopOrderNo();
        }
        if(orderBean.getInstant()==1){//及时单
            return shopOrderSn;
        }else{
            return shopOrderSn+"约";
        }
    }

    public static String getShopOrderSn(EvaluationBean evaluationBean){
        if(evaluationBean.getDeliveryTeam()== DeliveryTeam.MEITUAN){
            return "美团"+evaluationBean.getMtShopOrderNo();
        }
        String shopOrderSn = "";
        if(evaluationBean.getShopOrderNo()<10){
            shopOrderSn = "00"+evaluationBean.getShopOrderNo();
        }else if(evaluationBean.getShopOrderNo()>=10&&evaluationBean.getShopOrderNo()<100){
            shopOrderSn = "0"+evaluationBean.getShopOrderNo();
        }else{
            shopOrderSn = ""+evaluationBean.getShopOrderNo();
        }
        if(evaluationBean.getInstant()==1){//及时单
            return shopOrderSn;
        }else{
            return shopOrderSn+"约";
        }
    }



    //拼接个性化标签
    public static String getLabelStr(List<String> list){
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



    /**
     * 判断一组顺风单中是否与参考状态一致
     * @param sfGroupBean 顺风单组
     * @param produceStatus 参考状态
     * @return
     */
    public static boolean isSameStatus(SFGroupBean sfGroupBean,int produceStatus){
        for(OrderBean orderBean:sfGroupBean.getItemGroup()){
            if(orderBean.getProduceStatus()!=produceStatus){
                return false;
            }
        }
        return true;
    }

    //计算一组顺风单的总杯量
    public static int getSFOderTotalQutity(SFGroupBean sfGroupBean){
        if(sfGroupBean.getItemGroup()==null){
            return 0;
        }
        int sum=0;
        for(int i=0;i<sfGroupBean.getItemGroup().size();i++){
            sum+=getTotalQutity(sfGroupBean.getItemGroup().get(i));
        }
        return sum;
    }

    //计算顺风单列表的总杯量
    public static int getSFOrderTotalQutity(List<SFGroupBean> sfGroupBeanList){
        int cupCount = 0;
        for(int i=0;i<sfGroupBeanList.size();i++){
            cupCount+=getSFOderTotalQutity(sfGroupBeanList.get(i));
        }
        return cupCount;
    }


    //计算顺风单组列表的总单数
    public static int getGroupTotalCount(List<SFGroupBean> sfGroupBeanList){
        int sum = 0;
        for(int i = 0;i<sfGroupBeanList.size();i++){
            sum+=sfGroupBeanList.get(i).getItemGroup().size();
        }
        return sum;
    }

    //计算顺风单订单列表的的咖啡名和对应的杯数
    public static  Map<String,Integer> getSFBatchMap(List<OrderBean> orderList){
        Map<String,Integer> contentSFMap = new HashMap<>();
        for(int i=0;i<orderList.size();i++){
            List<ItemContentBean> itemList = orderList.get(i).getItems();
            for(int j=0;j<itemList.size();j++){
                ItemContentBean item = itemList.get(j);
                if(contentSFMap.containsKey(item.getProduct())){
                    int newCount = contentSFMap.get(item.getProduct())+item.getQuantity();
                    contentSFMap.put(item.getProduct(),newCount);
                }else {
                    contentSFMap.put(item.getProduct(),item.getQuantity());
                }
            }
        }
        return contentSFMap;
    }


    //顺风单组批量处理订单的咖啡内容信息
    public static String createSFPromptStr(Context context,SFGroupBean sfGroupBean){
        int cupCount = getSFOderTotalQutity(sfGroupBean);
        int orderCount = sfGroupBean.getItemGroup()==null?0:sfGroupBean.getItemGroup().size();
        List<Map.Entry<String, Integer>> list_map = new ArrayList<>(getSFBatchMap(sfGroupBean.getItemGroup()).entrySet());
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
        return context.getResources().getString(R.string.batch_sf_prompt,orderCount,cupCount,sb.toString());
        //    return "系统已将"+orderCount+"单合并在一起，共有"+cupCount+"杯咖啡待生产，生产时效为"+cupCount*2+"分钟\n建议生产顺序为 : "+sb.toString();
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
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String start = sdf.format(new Date(pob.getExpectedTime()));
                return start;
            }
        }else{
            if(pob.getInstant()==1){
                //及时单
                return "尽快送达";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String start = sdf.format(new Date(pob.getExpectedTime()));
                String end = sdf.format(new Date(pob.getExpectedTime()+30*60*1000));
                return start+"~"+end;
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
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String start = sdf.format(new Date(orderBean.getExpectedTime()));
                return start;
            }
        }else{
            if(orderBean.getInstant()==1){
                //及时单
                return "尽快送达";
            }else{
                //预约单
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String start = sdf.format(new Date(orderBean.getExpectedTime()));
                String end = sdf.format(new Date(orderBean.getExpectedTime()+30*60*1000));
                return start+"~"+end;
            }
        }

    }

    //针对当前时间送达计算订单的服务时效
    public static String getTimeToService(OrderBean orderBean){
        SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
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
        SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
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
            if(realReachTime<=T2){
                result = "良好";
            }else if(realReachTime>T2 && realReachTime<=T2+5*60*1000){
                result = "合格";
            }else if(realReachTime>T2+5*60*1000){
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



    //列表显示订单尾号
    public static String getSimpleOrderSnForPrint(String orderSn){
        if(TextUtils.isEmpty(orderSn)){
            return "";
        }

        if(orderSn.length()<=6){
            return "("+orderSn+")";
        }else {
            return "("+orderSn.substring(6)+")";
        }

    }

}
