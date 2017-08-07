package com.lyancafe.coffeeshop.common;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.map.Text;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintObject;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.http.Api;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/1/25.
 */
public class PrintHelper {

    private static final String TAG = "PrintHelper";
    private static String ip_print_order = "192.19.1.231";
    private static String ip_print_cup = "192.19.1.232";
    private static final int MSG_PING = 66;
    public static final int MSG_EXCEPTION = 67;
    private static PrintHelper mInstance;
    private OnPromptListener mlistener;
    private boolean printerIsAvailable = true;
    private  ThreadPoolExecutor mPoolExecutor;

    private PrintHelper() {
        Log.d(TAG,"PrintHelpter()");
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if(Api.BASE_URL.contains("cn")||Api.BASE_URL.contains("192.168")){
            ip_print_order = "192.168.1.229";
            ip_print_cup = "192.168.1.229";
        }else{
            ip_print_order = "192.19.1.231";
            ip_print_cup = "192.19.1.232";
        }
    }

    public static PrintHelper getInstance(){
        if(mInstance == null){
            mInstance = new PrintHelper();
        }
        return mInstance;
    }

    //根据订单的总杯数计算盒子数
    private int getTotalBoxAmount(int totalQuantity){
        if(totalQuantity%4==0){
            return totalQuantity/4;
        }else{
            return totalQuantity/4 + 1;
        }
    }
    //计算订单中的咖啡清单列表
    public ArrayList<String> getCoffeeList(OrderBean orderBean){
        ArrayList<String> coffeeList = new ArrayList<>();
        for(int i=0;i<orderBean.getItems().size();i++){
            ItemContentBean item = orderBean.getItems().get(i);
            int quantity = item.getQuantity();
            for(int j=0;j<quantity;j++){
                coffeeList.add(item.getProduct());
            }
        }
        return coffeeList;
    }


    //计算杯贴纸信息，生成打印数据模型
    private List<PrintOrderBean> calculatePinterOrderBeanList(OrderBean orderBean){
        List<PrintCupBean> hotCupList = new ArrayList<>();
        List<PrintCupBean> coolCupList = new ArrayList<>();
        calculatePinterCupBeanList(orderBean,hotCupList,coolCupList);
        ArrayList<PrintOrderBean> boxList = new ArrayList<>();
        int hotBoxAmount = getTotalBoxAmount(hotCupList.size());
        int coolBoxAmount = getTotalBoxAmount(coolCupList.size());
        int totalBoxAmount = hotBoxAmount+coolBoxAmount; //盒子总数
        Log.i(TAG,"hotBoxAmount = "+hotBoxAmount+" | coolBoxAmount = "+coolBoxAmount+" | totalBoxAmount = "+totalBoxAmount);
        int i = 0;      //盒子号
        for(i=0;i<hotCupList.size()/4;i++){
            PrintOrderBean bean = new PrintOrderBean(totalBoxAmount,i+1,4);
            bean.setCoffeeList(hotCupList.subList(i * 4, i * 4 + 4));
            bean.setOrderId(orderBean.getId());
            bean.setShopOrderNo(OrderHelper.getShopOrderSn(orderBean));
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

    //打印入口方法
    public void printOrderInfo(OrderBean orderBean){
        Log.d(TAG, "printOrderInfo");
        List<PrintOrderBean> printList = calculatePinterOrderBeanList(orderBean);
        Log.d(TAG, "printList.size  =" + printList.size());

        for(PrintOrderBean bean:printList){
            String printContent = getPrintOrderContent(bean);
            DoPrintOrder(printContent);
            Log.d(TAG, "打印盒子清单:" + bean.toString());
        }

        OrderHelper.addPrintedSet(CSApplication.getInstance(), orderBean.getOrderSn());
    }
    //把要打印的盒子小票信息组装成字符串
    private String getPrintOrderContent(PrintOrderBean bean){
        String order1 = "",order2 = "",order3 = "",order4 = "";
        List<PrintCupBean> coffeeList = bean.getCoffeeList();
        switch (coffeeList.size()){
            case 1:
                order1 = coffeeList.get(0).getCoffee()+" "+coffeeList.get(0).getLabel();
                break;
            case 2:
                order1 = coffeeList.get(0).getCoffee()+" "+coffeeList.get(0).getLabel();
                order2 = coffeeList.get(1).getCoffee()+" "+coffeeList.get(1).getLabel();
                break;
            case 3:
                order1 = coffeeList.get(0).getCoffee()+" "+coffeeList.get(0).getLabel();
                order2 = coffeeList.get(1).getCoffee()+" "+coffeeList.get(1).getLabel();
                order3 = coffeeList.get(2).getCoffee()+" "+coffeeList.get(2).getLabel();
                break;
            case 4:
                order1 = coffeeList.get(0).getCoffee()+" "+coffeeList.get(0).getLabel();
                order2 = coffeeList.get(1).getCoffee()+" "+coffeeList.get(1).getLabel();
                order3 = coffeeList.get(2).getCoffee()+" "+coffeeList.get(2).getLabel();
                order4 = coffeeList.get(3).getCoffee()+" "+coffeeList.get(3).getLabel();
                break;
        }

        String addressCMD, addr1,addr2,addr3;
        Log.d(TAG, "address len: " + bean.getAddress().length());
        int length = bean.getAddress().length();
        if (length <= 22) {
            addressCMD = "A100,300,0,230,1,1,N,\""+bean.getAddress()+"\""+"\n";
        } else if(length>22 && length<43){
            addr1 = bean.getAddress().substring(0, 22);
            addr2 = bean.getAddress().substring(22);
            addressCMD = "A100,300,0,230,1,1,N,\""+addr1+"\""+"\n" +
                        "A100,330,0,230,1,1,N,\""+addr2+"\""+"\n";
        }else{
            addr1 = bean.getAddress().substring(0, 22);
            addr2 = bean.getAddress().substring(22,43);
            addr3 = bean.getAddress().substring(43);
            addressCMD = "A100,300,0,230,1,1,N,\""+addr1+"\""+"\n" +
                        "A100,330,0,230,1,1,N,\""+addr2+"\""+"\n"+
                        "A100,360,0,230,1,1,N,\""+addr3+"\""+"\n";
        }

        return  "N"+"\n"+
                "q640"+"\n"+
                "Q400,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A20,30,0,230,2,2,N,\""+bean.getShopOrderNo()+"\""+"\n"+
                "A300,30,0,230,2,2,N,\""+bean.getLocalStr()+"\""+"\n"+           //杯数盒子信息
                "A20,100,0,230,1,1,N,\"订单编号:\""+"\n"+ //订单编号
                "A140,100,0,230,1,1,N,\""+bean.getOrderId()+"\""+"\n"+
                "A450,100,0,230,1,1,N,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\""+"\n"+
                "A20,120,0,230,1,1,N,\"-------------------------------------------------- \""+"\n"+
                "A20,150,0,230,1,1,N,\""+order1+"\""+"\n"+
                "A320,150,0,230,1,1,N,\""+order2+"\""+"\n"+
                "A20,200,0,230,1,1,N,\""+order3+"\""+"\n"+
                "A320,200,0,230,1,1,N,\""+order4+"\""+"\n"+
                "A20,230,0,230,1,1,N,\"-------------------------------------------------- \""+"\n"+
                "A20,260,0,230,1,1,N,\"收货人 \""+"\n"+
                "A120,260,0,230,1,1,N,\""+bean.getReceiverName()+"\""+"\n"+
                "A300,260,0,230,1,1,N,\"送达时间 \""+"\n"+
                "A420,260,0,230,1,1,N,\""+OrderHelper.getPeriodOfExpectedtime(bean)+"\""+"\n"+
                "A20,300,0,230,1,1,N,\"地址 \""+"\n"+
                addressCMD +                             //配送地址
                "P1"+"\n";

    }


    private  void DoPrintOrder(String printContent){
        Log.i(TAG,"DoPrintOrder");
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_order);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }



    //打印杯子贴纸信息入口方法
    public  void printOrderItems(OrderBean orderBean){
        List<PrintCupBean> hotPrintList = new ArrayList<>();
        List<PrintCupBean> coolPrintList = new ArrayList<>();
        calculatePinterCupBeanList(orderBean, hotPrintList, coolPrintList);
        Log.d(TAG, "printList.size = " + hotPrintList.size() + "+" + coolPrintList.size());

        for(PrintCupBean bean:hotPrintList){
            String printContent = getPrintCupContent(bean);
            DoPrintCup(printContent);
            Log.d(TAG, "打印杯贴纸:" + bean.toString());
        }
        for(PrintCupBean bean:coolPrintList){
            String printContent = getPrintCupContent(bean);
            DoPrintCup(printContent);
            Log.d(TAG, "打印杯贴纸:" + bean.toString());
        }
    }


    //计算杯子贴纸信息，生成打印数据模型
    private int calculatePinterCupBeanList(OrderBean orderBean,List<PrintCupBean> hotCuplist,List<PrintCupBean> coolCuplist){
        ArrayList<ItemContentBean> hotItemList = getCoolOrHotItemList(orderBean, true);
        ArrayList<ItemContentBean> coolItemList = getCoolOrHotItemList(orderBean, false);

        int hotTotalQuantity = getTotalQuantity(hotItemList);
        int coolTotalQuantity = getTotalQuantity(coolItemList);
        Log.e(TAG,"hotTotalQuantity = "+hotTotalQuantity+" | coolTotalQuantity = "+coolTotalQuantity);
        int hotBoxAmount = getTotalBoxAmount(hotTotalQuantity);
        int coolBoxAmount = getTotalBoxAmount(coolTotalQuantity);
        int boxAmount = hotBoxAmount + coolBoxAmount;
        Log.e(TAG,"hotBoxAmount = "+hotBoxAmount+" | coolBoxAmount = "+coolBoxAmount);
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
        return boxAmount;
    }

    private int getTotalQuantity(List<ItemContentBean> itemList) {
        int sum = 0;
        for(int i=0;i<itemList.size();i++){
            sum+=itemList.get(i).getQuantity();
        }
        return sum;
    }

    private ArrayList<ItemContentBean> getCoolOrHotItemList(OrderBean orderBean,boolean isHot) {
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


    //根据盒号计算当前盒中总的杯数
    private int getCupAmountPerBox(int boxNumber,int totalQuantity,int totalBoxAmount){
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
    //把要打印的杯子小票信息组装成字符串
    private String getPrintCupContent(PrintCupBean bean){
        String shopOrderSn = bean.getShopOrderNo();
        return  "N"+"\n"+
                "OD"+"\n"+
                "q240"+"\n"+
                "Q160,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A20,40,0,230,1,1,N,\""+shopOrderSn+"\""+"\n"+
                "A110,40,0,230,1,1,N,\""+bean.getBoxAmount()+"-"+bean.getBoxNumber()+"|"+bean.getCupAmount()+"-" +bean.getCupNumber()+"\""+"\n"+ //杯数盒子信息
                "A20,70,0,230,1,1,N,\""+bean.getCoffee()+"\""+"\n"+
                "A20,100,0,230,1,1,N,\""+bean.getLabel()+"\""+"\n"+
                "P1"+"\n";

    }


    private void DoPrintCup(String printContent){
        Log.d(TAG,"DoPrintCup");
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_cup);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }

    private class DoPrintRunnable implements Runnable {
        private String printerIP = "";
        private String printConent = "";

        private void setPrinterIP(String ip) {
            this.printerIP = ip;
        }

        private void setPrinterContent(String content) {
            this.printConent = content;
            Log.d(TAG,"printConent = "+printConent);
        }
        @Override
        public void run() {
            Log.i(TAG,"printerIsAvailable = "+printerIsAvailable);
            while (!printerIsAvailable) {
                try {
                    Thread.sleep(300);
                    Log.i(TAG, "sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String host = printerIP;
            int port = 9100;
            Socket client;
            printerIsAvailable = false;
            try {
                client = new Socket(host, port);
                Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
                writer.write(printConent);
                writer.flush();
                writer.close();
                client.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e(TAG, "UnknownHostException:"+e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException" + e.toString());
                ToastUtil.showToast(CSApplication.getInstance(),"打印机"+host+"无法连接");
            } finally {
                printerIsAvailable = true;
                Log.i(TAG,"run  finally");
            }
        }
    }


    public void setPromptlistener(OnPromptListener promptlistener){
        this.mlistener = promptlistener;
    }
    public interface OnPromptListener{

        void onPrompt(int type,String message);
    }

    //在字符串中每隔三个字符插入一个符号
    public String getSelfDefineOrderId(long orderId){
        String orderIdStr = String.valueOf(orderId);
        int length = orderIdStr.length();
        if(length<=3){
            return orderIdStr;
        }else if(length>3&&length<=6){
            return orderIdStr.substring(0,3)+"-"+orderIdStr.substring(3);
        }else if(length>6&&length<=9){
            return orderIdStr.substring(0,3)+"-"+orderIdStr.substring(3,6)+"-"+orderIdStr.substring(6);
        }
        return "";
    }

    //检查打印机是否ping得通
    public void checkPrinterStatus() {
        DoPingRunnable dpt = new DoPingRunnable();
        mPoolExecutor.execute(dpt);
    }

    private class DoPingRunnable implements Runnable {
        public void run() {
            boolean pingResult = ping(ip_print_order);
            if (pingResult) {
                mlistener.onPrompt(MSG_PING,ip_print_order+"在线");
            } else {
                mlistener.onPrompt(MSG_PING, ip_print_order + "无法连接");
            }

            pingResult = ping(ip_print_cup);
            if (pingResult) {
                mlistener.onPrompt(MSG_PING, ip_print_cup + "在线");
            } else {
                mlistener.onPrompt(MSG_PING, ip_print_cup + "无法连接");
            }
        }
    }

    private boolean ping(String destip) {
        int timeOut = 3000; //定义超时，表明该时间内连不上即认定为不可达，超时值不能太小。
        try {//ping功能
            boolean status = InetAddress.getByName(destip).isReachable(timeOut);
            Log.d(TAG, "Status = " + status);
            return status;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return false;
        }
    }

    //批量打印订单入口
    public void printBatchBoxes(List<OrderBean> orderList){
        for(OrderBean bean:orderList){
            printOrderInfo(bean);
        }
    }


    //批量打印杯贴纸入口
    public void printBatchCups(List<OrderBean> orderList){
        List<PrintCupBean> cupBeanList = calculateBatchCupList(orderList);
        List<PrintCupBean> sortedCupList = sortCupList(cupBeanList);

        for(PrintCupBean bean:sortedCupList){
            String printContent = getPrintCupContent(bean);
            DoPrintCup(printContent);
            Log.d(TAG, "打印杯贴纸:" + bean.toString());
        }

    }

    //生成批量打印的杯贴纸集合（按同种咖啡数量多到少排序）
    private List<PrintCupBean> calculateBatchCupList(List<OrderBean> orderList){
        List<PrintCupBean> batchCupList = new ArrayList<>();
        for(OrderBean orderBean:orderList){
            List<PrintCupBean> hotCupList = new ArrayList<>();
            List<PrintCupBean> coolCupList = new ArrayList<>();
            calculatePinterCupBeanList(orderBean,hotCupList,coolCupList);
            batchCupList.addAll(hotCupList);
            batchCupList.addAll(coolCupList);
        }
        sortCupList(batchCupList);
        return batchCupList;
    }

    //对集合按照同元素数量由多到少的顺序排序
    private List<PrintCupBean> sortCupList(List<PrintCupBean> batchCupList){
        List<PrintCupBean> sortedCupList = new ArrayList<>();
        Map<String,Integer> map = creteCoffeeNumberMap(batchCupList);
        List<Map.Entry<String, Integer>> list_data = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        Collections.sort(list_data, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return rhs.getValue() - lhs.getValue();
            }
        });
        for(Map.Entry<String,Integer> entry:list_data){
            String key = entry.getKey();
            sortedCupList.addAll(getCupListByName(key, batchCupList));
            Log.d(TAG, key + " * " + entry.getValue());
        }

        return sortedCupList;
    }
    //通过咖啡名获取包含该咖啡的bean列表
    private List<PrintCupBean> getCupListByName(String coffee,List<PrintCupBean> batchCupList){
        List<PrintCupBean> cupBeanList = new ArrayList<>();
        for(PrintCupBean cupBean:batchCupList){
            if(coffee.equals(cupBean.getCoffee())){
                cupBeanList.add(cupBean);
            }
        }
        return cupBeanList;
    }

    //生成合并订单的咖啡和对应数量的映射关系
    private Map<String,Integer> creteCoffeeNumberMap(List<PrintCupBean> batchCupList){
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(PrintCupBean cupBean:batchCupList){
            if(map.get(cupBean.getCoffee())==null){
                map.put(cupBean.getCoffee(), 1);
            }else{
                map.put(cupBean.getCoffee(), map.get(cupBean.getCoffee()) + 1);
            }

        }
        return map;
    }

    private  void DoPrintMaterial(String printContent){
        Log.d(TAG,"DoPrintMaterial");
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_order);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }



    private String getOverDueDate(int overdueDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
        Calendar nowDate = Calendar.getInstance();
        nowDate.add(Calendar.DAY_OF_MONTH,overdueDays);
        return sdf.format(nowDate.getTime());
    }


    //打印物料（大纸）
    public  void printMaterialBig(MaterialItem materialItem){
        String printMaterialContent =  "N"+"\n"+
                    "OD"+"\n"+
                    "q640"+"\n"+
                    "Q400,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n"+
                    "A60,150,0,230,3,4,N,\""+materialItem.getName()+"\""+"\n"+
                    "P1"+"\n";
        DoPrintMaterial(printMaterialContent);
    }


    //打印物料（小纸）
    public void printPasterSmall(MaterialItem materialItem){
        String pasterContent = "N"+"\n"+
                    "OD"+"\n"+
                    "q240"+"\n"+
                    "Q160,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n"+
                    "A10,20,0,230,1,1,N,\""+materialItem.getName()+"\""+"\n"+
                    "A10,55,0,230,1,1,N,\""+getOverDueDate(materialItem.getOverdueTime())+"\""+"\n"+
                    "A10,80,0,230,1,1,N,\"过期\""+"\n"+
                    "A10,120,0,230,1,1,N,\"原始到期:\""+"\n"+
                    "A120,120,0,230,1,1,N,\"____________\""+"\n"+
                    "P1"+"\n";
        DoPrintPaster(pasterContent);
    }

    //打印空白贴纸（小纸）
    public void printPasterSmallBlank(){
        String pasterContent = "N"+"\n"+
                "OD"+"\n"+
                "q240"+"\n"+
                "Q160,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A10,25,0,230,1,1,N,\"物料:\""+"\n"+
                "A70,25,0,230,1,1,N,\"_____________\""+"\n"+
                "A10,70,0,230,1,1,N,\"过期:\""+"\n"+
                "A70,70,0,230,1,1,N,\"_____________\""+"\n"+
                "A10,120,0,230,1,1,N,\"原始到期:\""+"\n"+
                "A120,120,0,230,1,1,N,\"____________\""+"\n"+
                "P1"+"\n";
        DoPrintPaster(pasterContent);
    }

    private  void DoPrintPaster(String printContent){
        Log.d(TAG,"DoPrintMaterial");
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_cup);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }


    //打印汇总
    private  void doPrintBatchInfo(String printContent){
        Log.d("xls","DoPrintBatchInfo ="+printContent);
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_order);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }


    private String getRemarkFlag(boolean isHaveRemark){
        if(isHaveRemark){
            return "  备";
        }else{
            return "";
        }
    }


    public void printBatchInfo(List<OrderBean> batchOrders){
        Map<String,Integer> coffeeMap = new HashMap<>();
        Map<String,Map<String,Integer>> recipeFittingsMap = new HashMap<>();
        for(OrderBean order:batchOrders){
            List<ItemContentBean> items = order.getItems();
            for(ItemContentBean item:items){
                if(!coffeeMap.containsKey(item.getProduct())){
                    coffeeMap.put(item.getProduct(),item.getQuantity());
                }else{
                    coffeeMap.put(item.getProduct(),coffeeMap.get(item.getProduct())+item.getQuantity());
                }

                //个性化口味
                String fittings = item.getRecipeFittings();
                if(!TextUtils.isEmpty(fittings)){
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
        List<PrintObject> printObjects = PrintObject.transformPrintObjects(coffeeMap,recipeFittingsMap);
        LogUtil.d("xls","recipeFittingsMap size = "+recipeFittingsMap.size());
        LogUtil.d("xls","printObject size = "+printObjects.size());
        if(printObjects.size()>0){
            for(int i=printObjects.size()-1;i>=0;i--){
                doPrintBatchInfo(printObjects.get(i).getPrintContent());
            }
        }

    }


}
