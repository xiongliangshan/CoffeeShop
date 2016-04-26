package com.lyancafe.coffeeshop.helper;

import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.xls.http.HttpUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/1/25.
 */
public class PrintHelper {

    private static final String TAG = "PrintHelpter";
    public static String ip_print_order = "192.19.1.231";
    public static String ip_print_cup = "192.19.1.232";
    public static final int MSG_PING = 66;
    public static final int MSG_EXCEPTION = 67;
    private static PrintHelper mInstance;
    private OnPromptListener mlistener;
    private boolean printerIsAvailable = true;
    private  ThreadPoolExecutor mPoolExecutor;

    private PrintHelper() {
        Log.d(TAG,"PrintHelpter()");
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if(HttpUtils.BASE_URL.contains("test")){
            ip_print_order = "192.168.1.231";
            ip_print_cup = "192.168.1.231";
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
    public int getTotalBoxAmount(int totalQuantity){
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

    //计算盒子小票信息，生成打印数据模型
    public List<PrintOrderBean> calculatePinterOrderBeanList(OrderBean orderBean){
        Log.d(TAG,"calculatePinterOrderBeanList,order:"+orderBean.toString());
        boolean isGiftBox = orderBean.getGift()==2?true:false;
        ArrayList<PrintOrderBean> boxList = new ArrayList<>();
        List<String> coffeeList = getCoffeeList(orderBean);
        int totalQuantity = coffeeList.size();
        Log.d(TAG,"totalQuantity :"+totalQuantity);
        int totalBoxAmount = getTotalBoxAmount(totalQuantity);
        List<ItemContentBean> itemContentBeans = orderBean.getItems();

        int i = 0;
        for(i=0;i<totalQuantity/4;i++){
            List<String> coffee = coffeeList.subList(i*4, i*4+4);
            PrintOrderBean bean = new PrintOrderBean(orderBean.getId(),isGiftBox,orderBean.getOrderSn(),(i+1),totalBoxAmount,4,
                    orderBean.getRecipient(),orderBean.getPhone(),orderBean.getAddress(),orderBean.getCourierName(),orderBean.getCourierPhone(),coffee);
            boxList.add(bean);
        }
        int left_cup = totalQuantity%4;
        Log.d(TAG,"left_cup = "+left_cup);
        if(left_cup>0){
            List<String> coffeeLeft = coffeeList.subList(i*4, coffeeList.size());
            Log.d(TAG,"coffeeLeft.size = "+coffeeLeft.size());
            PrintOrderBean bean = new PrintOrderBean(orderBean.getId(),isGiftBox,orderBean.getOrderSn(),(i+1),totalBoxAmount,left_cup,
                    orderBean.getRecipient(),orderBean.getPhone(),orderBean.getAddress(),orderBean.getCourierName(),orderBean.getCourierPhone(),coffeeLeft);
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
        OrderHelper.addPrintedSet(CoffeeShopApplication.getInstance(), orderBean.getOrderSn());
    }
    //把要打印的盒子小票信息组装成字符串
    public String getPrintOrderContent(PrintOrderBean bean){
        String order1 = "",order2 = "",order3 = "",order4 = "";
        List<String> coffeeList = bean.getCoffeeList();
        switch (coffeeList.size()){
            case 1:
                order1 = coffeeList.get(0);
                break;
            case 2:
                order1 = coffeeList.get(0);
                order2 = coffeeList.get(1);
                break;
            case 3:
                order1 = coffeeList.get(0);
                order2 = coffeeList.get(1);
                order3 = coffeeList.get(2);
                break;
            case 4:
                order1 = coffeeList.get(0);
                order2 = coffeeList.get(1);
                order3 = coffeeList.get(2);
                order4 = coffeeList.get(3);
                break;
        }

        String addressCMD, addr1, addr2,gift;
        Log.d(TAG, "address len: " + bean.getAddress().length());
        if (bean.getAddress().length() <= 22) {
            addressCMD = "A90,160,0,200,1,1,N,\""+bean.getAddress()+"\""+"\n";
        } else {
            addr1 = bean.getAddress().substring(0, 22);
            addr2 = bean.getAddress().substring(22);
            addressCMD = "A90,160,0,200,1,1,N,\""+addr1+"\""+"\n" +
                    "A90,190,0,200,1,1,N,\""+addr2+"\""+"\n";
        }
        String orderId = getSelfDefineOrderId(bean.getOrderId());
        if(bean.isGiftBox()){
            gift = "A480,40,0,200,2,2,N,\"礼盒\""+"\n";
        }else{
            gift = "A480,40,0,200,2,2,N,\""+"\n";
        }
        String text1 =
                "N"+"\n"+
                "q640"+"\n"+
                "Q400,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A10,50,0,200,1,1,N,\"订单号:\""+"\n"+ //订单号
                "A90,40,0,200,2,2,N,\""+orderId+"  "+bean.getBoxAmount()+"-" +bean.getBoxNumber()+"|"+bean.getCupAmount()+"\""+"\n"+ //杯数盒子信息
                 gift +
                "A10,90,0,200,1,1,N,\"收货人:\""+"\n"+
                "A90,100,0,200,2,2,N,\""+bean.getReceiverName()+" "+bean.getReceiverPhone()+"\""+"\n"+
                 addressCMD +                             //配送地址
                "A10,220,0,200,1,1,N,\"清单：\""+"\n"+
                "A50,250,0,200,1,1,N,\""+order1+"\""+"\n"+
                "A320,250,0,200,1,1,N,\""+order2+"\""+"\n"+
                "A50,280,0,200,1,1,N,\""+order3+"\""+"\n"+
                "A320,280,0,200,1,1,N,\""+order4+"\""+"\n"+
                "P1"+"\n";
        String text2 =
                "N"+"\n"+
                "q640"+"\n"+
                "Q400,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A10,50,0,200,1,1,N,\"订单号:\""+"\n"+ //订单号
                "A90,40,0,200,2,2,N,\""+orderId+"  "+bean.getBoxAmount()+"-" +bean.getBoxNumber()+"|"+bean.getCupAmount()+"\""+"\n"+ //杯数盒子信息
                 gift +
                "A10,90,0,200,1,1,N,\"收货人:\""+"\n"+
                "A90,100,0,200,2,2,N,\""+bean.getReceiverName()+" "+bean.getReceiverPhone()+"\""+"\n"+
                addressCMD +                             //配送地址
                "A10,220,0,200,1,1,N,\"清单：\""+"\n"+
                "A50,250,0,200,1,1,N,\""+order1+"\""+"\n"+
                "A320,250,0,200,1,1,N,\""+order2+"\""+"\n"+
                "A50,280,0,200,1,1,N,\""+order3+"\""+"\n"+
                "A320,280,0,200,1,1,N,\""+order4+"\""+"\n"+
                "A10,330,0,200,1,1,N,\"配送员："+bean.getDeliverName()+" "+bean.getDeliverPhone()+"\""+"\n"+
                "P1"+"\n";
        if(TextUtils.isEmpty(bean.getDeliverName())){
            return text1;
        }
        return text2;
    }
    public  void DoPrintOrder(String printContent){
        Log.d(TAG,"DoPrintOrder");
        while (printerIsAvailable == false) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_order);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }



    //打印杯子贴纸信息入口方法
    public  void printOrderItems(OrderBean orderBean){
        List<PrintCupBean> printList = calculatePinterCupBeanList(orderBean);
        Log.d(TAG,"printList.size = "+printList);
        for(PrintCupBean bean:printList){
            String printContent = getPrintCupContent(bean);
            DoPrintCup(printContent);
            Log.d(TAG, "打印杯贴纸:" + bean.toString());
        }
    }

    //计算杯子贴纸信息，生成打印数据模型
    public List<PrintCupBean> calculatePinterCupBeanList(OrderBean orderBean){
        ArrayList<PrintCupBean> cupList = new ArrayList<>();
        List<String> coffeeList = getCoffeeList(orderBean);
        int totalQuantity = coffeeList.size();
        int totalBoxAmount = getTotalBoxAmount(totalQuantity);
        int boxNumber = 1; //当前盒号
        int cupNumber = 1; //当前杯号
        int cupAmount = 0; //当前盒中总杯数

        for(int i=0;i<totalQuantity;i++){
            boxNumber = i/4+1;
            cupNumber = i%4+1;
            cupAmount = getCupAmountPerBox(boxNumber,totalQuantity,totalBoxAmount);
            PrintCupBean printCupBean =  new PrintCupBean(orderBean.getId(),boxNumber,totalBoxAmount,cupNumber,cupAmount,coffeeList.get(i));
            cupList.add(printCupBean);
        }
        return cupList;
    }

    //根据盒号计算当前盒中总的杯数
    public int getCupAmountPerBox(int boxNumber,int totalQuantity,int totalBoxAmount){
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
    public String getPrintCupContent(PrintCupBean bean){
        String orderId = getSelfDefineOrderId(bean.getOrderId());
        String text =
                "N"+"\n"+
                "OD"+"\n"+
                "q240"+"\n"+
                "Q160,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A30,40,0,200,1,1,N,\""+orderId+"\""+"\n"+
                "A30,70,0,200,1,1,N,\""+bean.getBoxAmount()+"-"+bean.getBoxNumber()+"|"+bean.getCupAmount()+"-" +bean.getCupNumber()+"\""+"\n"+ //杯数盒子信息
                "A30,100,0,200,1,1,N,\""+bean.getCoffee()+"\""+"\n"+
                "P1"+"\n";
        return text;
    }

    public void DoPrintCup(String printContent){
        Log.d(TAG,"DoPrintCup");
        while (printerIsAvailable == false) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_cup);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }

    public class DoPrintRunnable implements Runnable {
        private String printerIP = "";
        private String printConent = "";

        public void setPrinterIP(String ip) {
            this.printerIP = ip;
        }

        public void setPrinterContent(String content) {
            this.printConent = content;
            Log.d(TAG,"printConent = "+printConent);
        }
        @Override
        public void run() {
            String host = printerIP;
            int port = 9100;
            Socket client;
            printerIsAvailable = false;
            try {
                client = new Socket(host, port);
                Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
                String tempString = null;
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
                if(mlistener!=null){
                    mlistener.onPrompt(MSG_EXCEPTION,"打印机"+host+"无法连接");
                }
            } finally {
                printerIsAvailable = true;
                Log.d(TAG,"run  finally");
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

    public class DoPingRunnable implements Runnable {
        public void run() {
            boolean pingResult = ping(ip_print_order);
            if (pingResult == true) {
                mlistener.onPrompt(MSG_PING,ip_print_order+"在线");
            } else {
                mlistener.onPrompt(MSG_PING, ip_print_order + "无法连接");
            }

            pingResult = ping(ip_print_cup);
            if (pingResult == true) {
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
    public List<PrintCupBean> calculateBatchCupList(List<OrderBean> orderList){
        List<PrintCupBean> batchCupList = new ArrayList<>();
        for(OrderBean orderBean:orderList){
            batchCupList.addAll(calculatePinterCupBeanList(orderBean));
        }
        sortCupList(batchCupList);
        return batchCupList;
    }

    //对集合按照同元素数量由多到少的顺序排序
    public List<PrintCupBean> sortCupList(List<PrintCupBean> batchCupList){
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
    public List<PrintCupBean> getCupListByName(String coffee,List<PrintCupBean> batchCupList){
        List<PrintCupBean> cupBeanList = new ArrayList<>();
        for(PrintCupBean cupBean:batchCupList){
            if(coffee.equals(cupBean.getCoffee())){
                cupBeanList.add(cupBean);
            }
        }
        return cupBeanList;
    }

    //生成合并订单的咖啡和对应数量的映射关系
    public Map<String,Integer> creteCoffeeNumberMap(List<PrintCupBean> batchCupList){
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

    public  void DoPrintMaterial(String printContent){
        Log.d(TAG,"DoPrintMaterial");
        while (printerIsAvailable == false) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DoPrintRunnable dpt = new DoPrintRunnable();
        dpt.setPrinterIP(ip_print_order);
        dpt.setPrinterContent(printContent);
        mPoolExecutor.execute(dpt);
    }

    //物料内容
    public String getMaterialContent(MaterialBean materialBean){
        String text =
                        "N"+"\n"+
                        "OD"+"\n"+
                        "q640"+"\n"+
                        "Q600,24"+"\n"+
                        "S3"+"\n"+
                        "D8"+"\n"+
                        "A60,150,0,200,3,4,N,\""+materialBean.getName()+"\""+"\n"+
                        "P1"+"\n";
        return text;
    }

    //打印物料（大纸）
    public  void printMaterialBig(MaterialBean materialBean){
        String printMaterialContent = getMaterialContent(materialBean);
        DoPrintMaterial(printMaterialContent);
    }

    //贴纸内容
    public String getPasterContent(){
        String text =
                "N"+"\n"+
                        "OD"+"\n"+
                        "q240"+"\n"+
                        "Q160,16"+"\n"+
                        "S3"+"\n"+
                        "D8"+"\n"+
                        "A0,19,0,200,1,1,N,\"报废时间:\""+"\n"+
                        "A0,46,0,200,1,1,N,\"──────────────────────\""+"\n"+
                        "A0,72,0,200,1,1,N,\"品名:\""+"\n"+
                        "A110,72,0,200,1,1,N,\"签名:\""+"\n"+
                        "A0,98,0,200,1,1,N,\"──────────────────────\""+"\n"+
                        "A0,125,0,200,1,1,N,\"原始到期日:\""+"\n"+
                        "P1"+"\n";
        return text;
    }
    //打印贴纸（小纸）
    public void printPasterSmall(){
        String pasterContent = getPasterContent();
        DoPrintMaterial(pasterContent);
    }


}
