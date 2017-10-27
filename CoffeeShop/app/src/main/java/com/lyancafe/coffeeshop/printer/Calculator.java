package com.lyancafe.coffeeshop.printer;

import android.text.TextUtils;

import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/10/27.
 * 用于打印相关的计算
 */

public class Calculator {

    /**
     * 根据订单的总杯数计算盒子数
     * @param totalQuantity 总杯数
     * @return 盒子数
     */
    public static int getTotalBoxAmount(int totalQuantity){
        if(totalQuantity%4==0){
            return totalQuantity/4;
        }else{
            return totalQuantity/4 + 1;
        }
    }


    /**
     * 生成一个订单对应的大标签数据模型
     * @param orderBean  订单对象
     * @return 大标签对象集合
     */
    public static List<PrintOrderBean> calculatePinterOrderBeanList(OrderBean orderBean){
        List<PrintCupBean> hotCupList = new ArrayList<>();
        List<PrintCupBean> coolCupList = new ArrayList<>();
        calculatePinterCupBeanList(orderBean,hotCupList,coolCupList);
        ArrayList<PrintOrderBean> boxList = new ArrayList<>();
        int hotBoxAmount = getTotalBoxAmount(hotCupList.size());
        int coolBoxAmount = getTotalBoxAmount(coolCupList.size());
        int totalBoxAmount = hotBoxAmount+coolBoxAmount; //盒子总数
        int i = 0;      //盒子号
        for(i=0;i<hotCupList.size()/4;i++){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,i+1,4);
            bean.setCoffeeList(hotCupList.subList(i * 4, i * 4 + 4));
            bean.setOrderId(orderBean.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
            bean.setWxScan(orderBean.getWxScan());
            bean.setInstant(orderBean.getInstant());
            bean.setOrderSn(orderBean.getOrderSn());
            if(TextUtils.isEmpty(orderBean.getNotes()) && TextUtils.isEmpty(orderBean.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(orderBean.getDeliveryTeam());
            bean.setReceiverName(orderBean.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(orderBean));
            bean.setAddress(orderBean.getAddress());
            bean.setExpectedTime(orderBean.getExpectedTime());
            bean.setDeliverName(orderBean.getCourierName()==null?"":orderBean.getCourierName());
            boxList.add(bean);
        }
        int hot_left_cup = hotCupList.size()%4;
        if(hot_left_cup>0){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,i+1,hot_left_cup);
            bean.setCoffeeList(hotCupList.subList(i * 4, hotCupList.size()));
            bean.setOrderId(orderBean.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
            bean.setWxScan(orderBean.getWxScan());
            bean.setInstant(orderBean.getInstant());
            bean.setOrderSn(orderBean.getOrderSn());
            if(TextUtils.isEmpty(orderBean.getNotes()) && TextUtils.isEmpty(orderBean.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(orderBean.getDeliveryTeam());
            bean.setReceiverName(orderBean.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(orderBean));
            bean.setAddress(orderBean.getAddress());
            bean.setExpectedTime(orderBean.getExpectedTime());
            bean.setDeliverName(orderBean.getCourierName()==null?"":orderBean.getCourierName());
            boxList.add(bean);
        }

        int j = 0;      //盒子号
        for(j=0;j<coolCupList.size()/4;j++){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,hotBoxAmount+j+1,4);
            bean.setCoffeeList(coolCupList.subList(j * 4, j * 4 + 4));
            bean.setOrderId(orderBean.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
            bean.setWxScan(orderBean.getWxScan());
            bean.setInstant(orderBean.getInstant());
            bean.setOrderSn(orderBean.getOrderSn());
            if(TextUtils.isEmpty(orderBean.getNotes()) && TextUtils.isEmpty(orderBean.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(orderBean.getDeliveryTeam());
            bean.setReceiverName(orderBean.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(orderBean));
            bean.setAddress(orderBean.getAddress());
            bean.setExpectedTime(orderBean.getExpectedTime());
            bean.setDeliverName(orderBean.getCourierName()==null?"":orderBean.getCourierName());
            boxList.add(bean);
        }
        int cool_left_cup = coolCupList.size()%4;
        if(cool_left_cup>0){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,hotBoxAmount+j+1,cool_left_cup);
            bean.setCoffeeList(coolCupList.subList(j * 4, coolCupList.size()));
            bean.setOrderId(orderBean.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
            bean.setWxScan(orderBean.getWxScan());
            bean.setInstant(orderBean.getInstant());
            bean.setOrderSn(orderBean.getOrderSn());
            if(TextUtils.isEmpty(orderBean.getNotes()) && TextUtils.isEmpty(orderBean.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(orderBean.getDeliveryTeam());
            bean.setReceiverName(orderBean.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(orderBean));
            bean.setAddress(orderBean.getAddress());
            bean.setExpectedTime(orderBean.getExpectedTime());
            bean.setDeliverName(orderBean.getCourierName()==null?"":orderBean.getCourierName());
            boxList.add(bean);
        }


        return boxList;
    }


    /**
     * 生产一个订单对应的小标签数据模型
     * @param orderBean
     * @param hotCuplist
     * @param coolCuplist
     */
    public static void calculatePinterCupBeanList(OrderBean orderBean,List<PrintCupBean> hotCuplist,List<PrintCupBean> coolCuplist){
        ArrayList<ItemContentBean> hotItemList = getCoolOrHotItemList(orderBean, true);
        ArrayList<ItemContentBean> coolItemList = getCoolOrHotItemList(orderBean, false);

        int hotTotalQuantity = getTotalQuantity(hotItemList);
        int coolTotalQuantity = getTotalQuantity(coolItemList);
        int hotBoxAmount = getTotalBoxAmount(hotTotalQuantity);
        int coolBoxAmount = getTotalBoxAmount(coolTotalQuantity);
        int boxAmount = hotBoxAmount + coolBoxAmount;
        int pos = 0;
        for(int i=0;i<hotItemList.size();i++){
            ItemContentBean item = hotItemList.get(i);
            int quantity = item.getQuantity();
            for(int j=0;j<quantity;j++){
                int boxNumber = pos/4+1;    //当前盒号
                int cupNumber = pos%4+1;    //当前杯号
                int cupAmount = getCupAmountPerBox(boxNumber,hotTotalQuantity,hotBoxAmount);  //当前盒中总杯数

                PrintCupBean printCupBean = new PrintCupBean(boxAmount,boxNumber,cupAmount,cupNumber);
                printCupBean.setLabel(item.getRecipeFittings());
                printCupBean.setOrderId(orderBean.getId());
                printCupBean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
                printCupBean.setInstant(orderBean.getInstant());
                printCupBean.setCoffee(item.getProduct());
                printCupBean.setColdHotProperty(item.getColdHotProperty());
                hotCuplist.add(printCupBean);

                pos++;
            }
        }
        int index = 0;
        for(int i=0;i<coolItemList.size();i++){
            ItemContentBean item = coolItemList.get(i);
            int quantity = item.getQuantity();
            for(int j=0;j<quantity;j++){
                int boxNumber = index/4+1;
                int cupNumber = index%4+1;
                int cupAmount = getCupAmountPerBox(boxNumber,coolTotalQuantity,coolBoxAmount);

                PrintCupBean printCupBean = new PrintCupBean(boxAmount,boxNumber+hotBoxAmount,cupAmount,cupNumber);
                printCupBean.setLabel(item.getRecipeFittings());
                printCupBean.setOrderId(orderBean.getId());
                printCupBean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
                printCupBean.setInstant(orderBean.getInstant());
                printCupBean.setCoffee(item.getProduct());
                printCupBean.setColdHotProperty(item.getColdHotProperty());
                coolCuplist.add(printCupBean);

                index++;
            }
        }
    }

    /**
     * 根据盒号计算当前盒中的中杯数
     * @param boxNumber
     * @param totalQuantity
     * @param totalBoxAmount
     * @return
     */
    public static int getCupAmountPerBox(int boxNumber,int totalQuantity,int totalBoxAmount){
        if(totalQuantity%4==0){
            return 4;
        }else{
            if(boxNumber<totalBoxAmount){
                return 4;
            }else{
                return totalQuantity%4;
            }
        }
    }


    /**
     * 计算一个订单的总杯数
     * @param itemList
     * @return
     */
    public static int getTotalQuantity(List<ItemContentBean> itemList) {
        int sum = 0;
        for(int i=0;i<itemList.size();i++){
            sum+=itemList.get(i).getQuantity();
        }
        return sum;
    }


    /**
     * 计算一个订单对应冷热属性的咖啡清单
     * @param orderBean
     * @param isHot
     * @return
     */
    public static ArrayList<ItemContentBean> getCoolOrHotItemList(OrderBean orderBean,boolean isHot) {
        ArrayList<ItemContentBean> itemList = new ArrayList<>();
        for(int i=0;i<orderBean.getItems().size();i++){
            ItemContentBean itemContentBean = orderBean.getItems().get(i);
            if(isHot){
                if(itemContentBean.getColdHotProperty()!=1){
                    itemList.add(itemContentBean);
                }

            }else{
                if(itemContentBean.getColdHotProperty()==1){
                    itemList.add(itemContentBean);
                }
            }
        }
        return itemList;
    }


    /**
     * 生成批量打印的小标签集合（按同种咖啡数量多到少排序）
     * @param orderList
     * @return
     */
    public static List<PrintCupBean> calculateBatchCupList(List<OrderBean> orderList){
        List<PrintCupBean> batchCupList = new ArrayList<>();
        for(OrderBean orderBean:orderList){
            List<PrintCupBean> hotCupList = new ArrayList<>();
            List<PrintCupBean> coolCupList = new ArrayList<>();
            calculatePinterCupBeanList(orderBean,hotCupList,coolCupList);
            batchCupList.addAll(hotCupList);
            batchCupList.addAll(coolCupList);
        }
        Collections.sort(batchCupList, new Comparator<PrintCupBean>() {
            @Override
            public int compare(PrintCupBean o1, PrintCupBean o2) {
                return (int) (o1.getOrderId()-o2.getOrderId());
            }
        });
        return batchCupList;
    }



    /**
     * 通过咖啡名获取包含该咖啡的小标签数据集合
     * @param coffee
     * @param batchCupList
     * @return
     */
    public static List<PrintCupBean> getCupListByName(String coffee,List<PrintCupBean> batchCupList){
        List<PrintCupBean> cupBeanList = new ArrayList<>();
        for(PrintCupBean cupBean:batchCupList){
            if(coffee.equals(cupBean.getCoffee())){
                cupBeanList.add(cupBean);
            }
        }
        return cupBeanList;
    }


    /**
     * 根据保质期计算过期时间
     * @param overdueDays
     * @return
     */
    public static String getOverDueDate(int overdueDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        Calendar nowDate = Calendar.getInstance();
        if(overdueDays<=0){
            nowDate.add(Calendar.DAY_OF_MONTH,1);
            nowDate.set(Calendar.HOUR_OF_DAY,0);
            nowDate.set(Calendar.MINUTE,0);

        }else{
            nowDate.add(Calendar.DAY_OF_MONTH,overdueDays);
        }
        return sdf.format(nowDate.getTime());
    }

}
