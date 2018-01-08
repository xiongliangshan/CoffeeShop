package com.lyancafe.coffeeshop.printer;

import android.text.TextUtils;

import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.bean.Product;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 * 用于打印相关的计算
 */

public class Calculator {

    private static final String TAG = "Calculator";

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
     * 计算新版盒子数
     * @param totalQuantity
     * @return
     */
    public static int caculateBoxAmount(int totalQuantity){
        if(totalQuantity%2==0){
            return totalQuantity/2;
        }else {
            return totalQuantity/2 + 1;
        }
    }


    /**
     * 生成一个订单对应的大标签数据模型
     * @param orderBean  订单对象
     * @return 大标签对象集合
     */
    public static List<PrintOrderBean> calculatePinterOrderBeanList(OrderBean orderBean){
        LogUtil.d(TAG,"calculatePinterOrderBeanList");
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
            bean.setCheckAddress(orderBean.getCheckAddress());
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
            bean.setCheckAddress(orderBean.getCheckAddress());
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
            bean.setCheckAddress(orderBean.getCheckAddress());
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
            bean.setCheckAddress(orderBean.getCheckAddress());
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
     * 只有一杯盒，两杯盒的情况
     * @param order
     * @return
     */
    public static List<PrintOrderBean> calculateBigLabelObjects(OrderBean order){
        LogUtil.d(TAG,"calculateBigLabelObjects");
        List<PrintCupBean> hotCupList = new ArrayList<>();
        List<PrintCupBean> coolCupList = new ArrayList<>();
        calculateSmallLabelObjects(order,hotCupList,coolCupList);
        ArrayList<PrintOrderBean> boxList = new ArrayList<>();
        int hotBoxAmount = caculateBoxAmount(hotCupList.size());
        int coolBoxAmount = caculateBoxAmount(coolCupList.size());
        int totalBoxAmount = hotBoxAmount+coolBoxAmount; //盒子总数
        int i = 0;      //盒子号
        for(i=0;i<hotCupList.size()/2;i++){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,i+1,2);
            bean.setCoffeeList(hotCupList.subList(i * 2, i * 2 + 2));
            bean.setOrderId(order.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(order));
            bean.setWxScan(order.getWxScan());
            bean.setInstant(order.getInstant());
            bean.setOrderSn(order.getOrderSn());
            bean.setCheckAddress(order.getCheckAddress());
            if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(order.getDeliveryTeam());
            bean.setReceiverName(order.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(order));
            bean.setAddress(order.getAddress());
            bean.setExpectedTime(order.getExpectedTime());
            bean.setDeliverName(order.getCourierName()==null?"":order.getCourierName());
            boxList.add(bean);
        }
        int hot_left_cup = hotCupList.size()%2;
        if(hot_left_cup!=0){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,i+1,hot_left_cup);
            bean.setCoffeeList(hotCupList.subList(i * 2, hotCupList.size()));
            bean.setOrderId(order.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(order));
            bean.setWxScan(order.getWxScan());
            bean.setInstant(order.getInstant());
            bean.setOrderSn(order.getOrderSn());
            bean.setCheckAddress(order.getCheckAddress());
            if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(order.getDeliveryTeam());
            bean.setReceiverName(order.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(order));
            bean.setAddress(order.getAddress());
            bean.setExpectedTime(order.getExpectedTime());
            bean.setDeliverName(order.getCourierName()==null?"":order.getCourierName());
            boxList.add(bean);
        }

        int j = 0;      //盒子号
        for(j=0;j<coolCupList.size()/2;j++){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,hotBoxAmount+j+1,2);
            bean.setCoffeeList(coolCupList.subList(j * 2, j * 2 + 2));
            bean.setOrderId(order.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(order));
            bean.setWxScan(order.getWxScan());
            bean.setInstant(order.getInstant());
            bean.setOrderSn(order.getOrderSn());
            bean.setCheckAddress(order.getCheckAddress());
            if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(order.getDeliveryTeam());
            bean.setReceiverName(order.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(order));
            bean.setAddress(order.getAddress());
            bean.setExpectedTime(order.getExpectedTime());
            bean.setDeliverName(order.getCourierName()==null?"":order.getCourierName());
            boxList.add(bean);
        }
        int cool_left_cup = coolCupList.size()%2;
        if(cool_left_cup!=0){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,hotBoxAmount+j+1,cool_left_cup);
            bean.setCoffeeList(coolCupList.subList(j * 2, coolCupList.size()));
            bean.setOrderId(order.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(order));
            bean.setWxScan(order.getWxScan());
            bean.setInstant(order.getInstant());
            bean.setOrderSn(order.getOrderSn());
            bean.setCheckAddress(order.getCheckAddress());
            if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
                bean.setIsHaveRemarks(false);
            }else{
                bean.setIsHaveRemarks(true);
            }
            bean.setDeliveryTeam(order.getDeliveryTeam());
            bean.setReceiverName(order.getRecipient());
            bean.setReceiverPhone(OrderHelper.getHidePhone(order));
            bean.setAddress(order.getAddress());
            bean.setExpectedTime(order.getExpectedTime());
            bean.setDeliverName(order.getCourierName()==null?"":order.getCourierName());
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
                printCupBean.setProduceProcess(item.getProduceProcess());
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
                printCupBean.setProduceProcess(item.getProduceProcess());
                coolCuplist.add(printCupBean);

                index++;
            }
        }
    }

    /**
     * 针对新版盒子
     * @param orderBean
     * @param hotCuplist
     * @param coolCuplist
     */
    public static void calculateSmallLabelObjects(OrderBean orderBean,List<PrintCupBean> hotCuplist,List<PrintCupBean> coolCuplist){
        ArrayList<ItemContentBean> hotItemList = getCoolOrHotItemList(orderBean, true);
        ArrayList<ItemContentBean> coolItemList = getCoolOrHotItemList(orderBean, false);

        int hotTotalQuantity = getTotalQuantity(hotItemList);
        int coolTotalQuantity = getTotalQuantity(coolItemList);
        int hotBoxAmount = caculateBoxAmount(hotTotalQuantity);
        int coolBoxAmount = caculateBoxAmount(coolTotalQuantity);
        int boxAmount = hotBoxAmount + coolBoxAmount;
        int pos = 0;
        for(int i=0;i<hotItemList.size();i++){
            ItemContentBean item = hotItemList.get(i);
            int quantity = item.getQuantity();
            for(int j=0;j<quantity;j++){
                int boxNumber = pos/2+1;    //当前盒号
                int cupNumber = pos%2+1;    //当前杯号
                int cupAmount = caculateCupAmountPerBox(boxNumber,hotTotalQuantity,hotBoxAmount);  //当前盒中总杯数

                PrintCupBean printCupBean = new PrintCupBean(boxAmount,boxNumber,cupAmount,cupNumber);
                printCupBean.setLabel(item.getRecipeFittings());
                printCupBean.setOrderId(orderBean.getId());
                printCupBean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
                printCupBean.setInstant(orderBean.getInstant());
                printCupBean.setCoffee(item.getProduct());
                printCupBean.setColdHotProperty(item.getColdHotProperty());
                printCupBean.setProduceProcess(item.getProduceProcess());
                hotCuplist.add(printCupBean);

                pos++;
            }
        }
        int index = 0;
        for(int i=0;i<coolItemList.size();i++){
            ItemContentBean item = coolItemList.get(i);
            int quantity = item.getQuantity();
            for(int j=0;j<quantity;j++){
                int boxNumber = index/2+1;
                int cupNumber = index%2+1;
                int cupAmount = caculateCupAmountPerBox(boxNumber,coolTotalQuantity,coolBoxAmount);

                PrintCupBean printCupBean = new PrintCupBean(boxAmount,boxNumber+hotBoxAmount,cupAmount,cupNumber);
                printCupBean.setLabel(item.getRecipeFittings());
                printCupBean.setOrderId(orderBean.getId());
                printCupBean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
                printCupBean.setInstant(orderBean.getInstant());
                printCupBean.setCoffee(item.getProduct());
                printCupBean.setColdHotProperty(item.getColdHotProperty());
                printCupBean.setProduceProcess(item.getProduceProcess());
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
     * 针对新版
     * @param boxNumber
     * @param totalQuantity
     * @param totalBoxAmount
     * @return
     */
    public static int caculateCupAmountPerBox(int boxNumber,int totalQuantity,int totalBoxAmount){
        if(totalQuantity%2==0){
            return 2;
        }else{
            if(boxNumber<totalBoxAmount){
                return 2;
            }else{
                return totalQuantity%2;
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

    public static String formatLabel(String label){
        if(TextUtils.isEmpty(label)){
            return "";
        }else{
            return "("+label+")";
        }
    }

    public static String getCheckShopNo(PrintOrderBean bean){
        if(bean.isCheckAddress()){
            return "*"+bean.getShopOrderNo()+"*";
        }else{
            return bean.getShopOrderNo();
        }
    }

    //计算品类和对应数量的map
    public static void caculateItems(List<OrderBean> orderBeanList, Map<String,Product> productMap, Map<String,Map<String,Integer>> recipeFittingsMap){
        for(OrderBean order:orderBeanList){
            List<ItemContentBean> items = order.getItems();
            for(ItemContentBean item:items){
                String name = item.getProduct();
                Product product = productMap.get(name);
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
                    productMap.put(name,product);
                }else{
                    product.setCount(product.getCount()+item.getQuantity());
                }


                //个性化口味
                String fittings = item.getRecipeFittings();
                if(!TextUtils.isEmpty(fittings)&& "少冰".equals(fittings)){
                    Map<String,Integer> fittingsMap = null;
                    if(recipeFittingsMap.containsKey(item.getProduct())){
                        fittingsMap = recipeFittingsMap.get(item.getProduct());
                    }else{
                        fittingsMap = new HashMap<>();
                        recipeFittingsMap.put(item.getProduct(),fittingsMap);
                    }

                    if(fittingsMap.containsKey(fittings)){
                        fittingsMap.put(fittings,fittingsMap.get(fittings)+1);
                    }else{
                        fittingsMap.put(fittings,1);
                    }

                }

            }
        }
    }

}
