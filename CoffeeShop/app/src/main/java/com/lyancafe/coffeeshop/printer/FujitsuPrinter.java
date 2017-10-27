package com.lyancafe.coffeeshop.printer;

import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintObject;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.FinishedOrderSortComparator;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */

public class FujitsuPrinter implements NetPrint {

    private static final String TAG = "FujitsuPrinter";
    private String bigLabelIP;
    private String smallLabelIP;
    private int port;

    public FujitsuPrinter(String bigLabelIP, String smallLabelIP, int port) {
        this.bigLabelIP = bigLabelIP;
        this.smallLabelIP = smallLabelIP;
        this.port = port;
    }

    @Override
    public void printSummaryInfo(List<OrderBean> orderBeanList) {
        Map<String,Integer> coffeeMap = new HashMap<>();
        Map<String,Map<String,Integer>> recipeFittingsMap = new HashMap<>();
        for(OrderBean order:orderBeanList){
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
        List<PrintSummaryObject> printObjects = PrintSummaryObject.transformPrintObjects(coffeeMap,recipeFittingsMap);
        if(printObjects.size()>0){
            for(int i=printObjects.size()-1;i>=0;i--){
                writeCommand(bigLabelIP,port,printObjects.get(i).getPrintContent());
            }
        }
    }

    @Override
    public void printBigLabel(OrderBean orderBean) {
        List<PrintOrderBean> printList = Calculator.calculatePinterOrderBeanList(orderBean);
        for(PrintOrderBean bean:printList){
            String printCommand = OTCForBigLabel(bean);
            writeCommand(bigLabelIP,port,printCommand);
        }
        OrderHelper.addPrintedSet(CSApplication.getInstance(), orderBean.getOrderSn());
    }

    @Override
    public void printBigLabels(List<OrderBean> orderBeanList) {
        Collections.sort(orderBeanList,new FinishedOrderSortComparator());
        for(OrderBean bean:orderBeanList){
            printBigLabel(bean);
        }
    }

    @Override
    public void printSmallLabel(OrderBean orderBean) {
        List<PrintCupBean> hotPrintList = new ArrayList<>();
        List<PrintCupBean> coolPrintList = new ArrayList<>();
        Calculator.calculatePinterCupBeanList(orderBean, hotPrintList, coolPrintList);

        for(PrintCupBean bean:hotPrintList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);
        }
        for(PrintCupBean bean:coolPrintList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);

        }
    }

    @Override
    public void printSmallLabels(List<OrderBean> orderBeanList) {
        LogUtil.d(TAG,"printSmallLabels");
        List<PrintCupBean> cupBeanList = Calculator.calculateBatchCupList(orderBeanList);
        for(PrintCupBean bean:cupBeanList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);
        }
    }

    @Override
    public void printTimeControlPaster(MaterialItem materialItem) {
        String pasterContent =
                "SIZE 30 mm, 20 mm\n" +
                "GAP 3 mm, 0 mm\n" +
                "SET RIBBON OFF\n" +
                "DIRECTION 1,0\n" +
                "CLS\n" +
                "TEXT 10,20,\"TSS24.BF2\",0,1,1,\""+materialItem.getName()+"\"\n" +
                "TEXT 10,55,\"TSS24.BF2\",0,1,1,\""+Calculator.getOverDueDate(materialItem.getOverdueTime())+"\"\n" +
                "TEXT 10,80,\"TSS24.BF2\",0,1,1,\"过期\"\n" +
                "TEXT 10,120,\"TSS24.BF2\",0,1,1,\"原始到期:\"\n" +
                "TEXT 120,120,\"TSS24.BF2\",0,1,1,\"_____________\"\n" +
                "PRINT 1,1\n";
        writeCommand(smallLabelIP,port,pasterContent);
    }

    @Override
    public void printBlankPaster() {
        String pasterContent =
                        "SIZE 30 mm, 20 mm\n" +
                        "GAP 3 mm, 0 mm\n" +
                        "SET RIBBON OFF\n" +
                        "DIRECTION 1,0\n" +
                        "CLS\n" +
                        "TEXT 10,25,\"TSS24.BF2\",0,1,1,\"物料:\"\n" +
                        "TEXT 70,25,\"TSS24.BF2\",0,1,1,\"_____________\"\n" +
                        "TEXT 10,70,\"TSS24.BF2\",0,1,1,\"过期:\"\n" +
                        "TEXT 70,70,\"TSS24.BF2\",0,1,1,\"_____________\"\n" +
                        "TEXT 10,120,\"TSS24.BF2\",0,1,1,\"原始到期:\"\n" +
                        "TEXT 120,120,\"TSS24.BF2\",0,1,1,\"_____________\"\n" +
                        "PRINT 1,1\n";
        writeCommand(smallLabelIP,port,pasterContent);
    }

    @Override
    public void printMaterialBigLabel(MaterialItem materialItem) {
        String printMaterialContent =
                "SIZE 80 mm, 49 mm\n" +
                "GAP 2 mm, 0 mm\n" +
                "SET RIBBON OFF\n" +
                "DIRECTION 1,0\n" +
                "CLS\n" +
                "TEXT 40,150,\"TSS32.BF2\",0,3,3,\""+ materialItem.getName() +"\"\n" +
                "PRINT 1,1\n";
        writeCommand(bigLabelIP,port,printMaterialContent);
    }

    @Override
    public String OTCForBigLabel(PrintOrderBean bean) {
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
        int length = bean.getAddress().length();
        if (length <= 22) {
            addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+bean.getAddress()+"\""+"\n";
        } else if(length>22 && length<43){
            addr1 = bean.getAddress().substring(0, 22);
            addr2 = bean.getAddress().substring(22);
            addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
                    "TEXT 90,310,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n";
        }else{
            addr1 = bean.getAddress().substring(0, 22);
            addr2 = bean.getAddress().substring(22,43);
            addr3 = bean.getAddress().substring(43);
            addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
                    "TEXT 90,310,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n"+
                    "TEXT 90,340,\"TSS24.BF2\",0,1,1,\""+addr3+"\""+"\n";
        }

        return  "SIZE 80 mm, 49 mm\n" +
                "GAP 2 mm, 0 mm\n" +
                "SET RIBBON OFF\n" +
                "DIRECTION 1,0\n" +
                "CLS\n" +
                "TEXT 10,10,\"TSS24.BF2\",0,2,2,\""+ bean.getShopOrderNo() +"\"\n" +
                "TEXT 300,10,\"TSS24.BF2\",0,2,2,\""+ bean.getLocalStr() +"\"\n" +
                "TEXT 10,80,\"TSS24.BF2\",0,1,1,\"订单编号:\"\n" +
                "TEXT 130,80,\"TSS24.BF2\",0,1,1,\""+bean.getOrderId()+OrderHelper.getWxScanStrForPrint(bean)+"\"\n" +
                "TEXT 440,80,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\"\n" +
                "TEXT 10,100,\"TSS24.BF2\",0,1,1,\"----------------------------------------------------\"\n" +
                "TEXT 10,130,\"TSS24.BF2\",0,1,1,\""+order1+"\""+"\n"+
                "TEXT 310,130,\"TSS24.BF2\",0,1,1,\""+order2+"\""+"\n"+
                "TEXT 10,180,\"TSS24.BF2\",0,1,1,\""+order3+"\""+"\n"+
                "TEXT 310,180,\"TSS24.BF2\",0,1,1,\""+order4+"\""+"\n"+
                "TEXT 10,210,\"TSS24.BF2\",0,1,1,\"----------------------------------------------------\"\n" +
                "TEXT 10,240,\"TSS24.BF2\",0,1,1,\"收货人:\"\n" +
                "TEXT 110,240,\"TSS24.BF2\",0,1,1,\""+bean.getReceiverName()+"\"\n" +
                "TEXT 290,240,\"TSS24.BF2\",0,1,1,\"送达时间:\"\n" +
                "TEXT 410,240,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPeriodOfExpectedtime(bean)+"\"\n" +
                "TEXT 10,280,\"TSS24.BF2\",0,1,1,\"地址:\"\n" +
                addressCMD +
                "PRINT 1,1\n";

    }

    @Override
    public String OTCForSmallLabel(PrintCupBean bean) {
        return  "SIZE 30 mm, 20 mm\n" +
                "GAP 3 mm, 0 mm\n" +
                "SET RIBBON OFF\n" +
                "DIRECTION 1,0\n" +
                "CLS\n" +
                "TEXT 20,40,\"TSS24.BF2\",0,1,1,\""+ bean.getShopOrderNo() +"\"\n" +
                "TEXT 110,40,\"TSS24.BF2\",0,1,1,\""+bean.getBoxAmount()+"-"+bean.getBoxNumber()+"|"+bean.getCupAmount()+"-" +bean.getCupNumber()+"\"\n" +
                "TEXT 20,70,\"TSS24.BF2\",0,1,1,\""+bean.getCoffee()+"\"\n" +
                "TEXT 20,100,\"TSS24.BF2\",0,1,1,\""+bean.getLabel()+"\"\n" +
                "PRINT 1,1\n";
    }

    @Override
    public void checkPrinterStatus(String ip, int port) {

    }

    @Override
    public void writeCommand(String ip, int port, String command) {
        Log.d(TAG,Thread.currentThread().getName()+" writeCommand,command = "+command);
        Socket client;
        try {
            client = new Socket(ip, port);
            Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
            writer.write(command);
            writer.flush();
            writer.close();
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHostException:"+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException" + e.toString());
            ToastUtil.showToast(CSApplication.getInstance(),TAG+"打印机"+ip+":"+port+"无法连接");
        }
    }


    /**
     * 汇总信息数据模型
     */
    static class PrintSummaryObject{
        private String start;
        private List<String> content;
        private String end;

        public PrintSummaryObject() {
            this.start = "SIZE 80 mm, 49 mm\n" +
                    "GAP 2 mm, 0 mm\n" +
                    "SET RIBBON OFF\n" +
                    "DIRECTION 1,0\n";

            this.end = "PRINT 1,1\n";
            this.content = new ArrayList<>();
        }

        public static List<PrintSummaryObject> transformPrintObjects(Map<String,Integer> coffeeMap, Map<String,Map<String,Integer>> recipeFittingsMap) {

            List<PrintSummaryObject> printObjects = new ArrayList<>();
            if(coffeeMap.size()==0){
                return printObjects;
            }
            Iterator<String> iterator = coffeeMap.keySet().iterator();
            int coffeeSize = coffeeMap.size();
            int fittingSize = getMapSize(recipeFittingsMap);
            int totalSize = coffeeSize+fittingSize;
            int size = totalSize%6==0?totalSize/6:totalSize/6+1;
            for(int i=0;i<size;i++){
                printObjects.add(new PrintSummaryObject());
            }
            int l = 30;
            int t = 30;
            int i = 0;
            while (iterator.hasNext()){
                String name = iterator.next();
                LogUtil.d("xls",name+" x "+coffeeMap.get(name));
//                printObjects.get(i/6).getContent().add("A"+l+","+t+",0,230,2,2,N,\""+name+" x "+coffeeMap.get(name)+"\""+"\n");
                printObjects.get(i/6).getContent().add("TEXT "+l+","+t+",\"TSS24.BF2\",0,2,2,\""+name+" x "+coffeeMap.get(name)+"\"\n");
                t+=60;
                i++;
                if(i%6==0){
                    l = 30;
                    t = 30;
                }

                Map<String ,Integer> fittingsMap = recipeFittingsMap.get(name);
                if(fittingsMap!=null){
                    Iterator<String> iterator1 = fittingsMap.keySet().iterator();
                    while (iterator1.hasNext()){

                        String fitting = iterator1.next();
//                        printObjects.get(i/6).getContent().add("A"+(l+150)+","+t+",0,230,1,1,N,\""+fitting+" x "+fittingsMap.get(fitting)+"\""+"\n");
                        printObjects.get(i/6).getContent().add("TEXT "+(l+150)+","+t+",\"TSS24.BF2\",0,1,1,\""+fitting+" x "+fittingsMap.get(fitting)+"\"\n");
                        t+=60;
                        i++;
                        if(i%6==0){
                            l = 30;
                            t = 30;
                        }
                    }
                }


            }

            //构造头和尾
            PrintSummaryObject start = new PrintSummaryObject();
            start.getContent().add("TEXT 0,200,\"TSS24.BF2\",0,2,2,\"----------生产汇总------------\""+"\n");
            PrintSummaryObject end = new PrintSummaryObject();
            end.getContent().add("TEXT 0,200,\"TSS24.BF2\",0,2,2,\"----------生产汇总------------\""+"\n");
            printObjects.add(0,start);
            printObjects.add(end);
            return printObjects;
        }

        public static int getMapSize(Map<String,Map<String,Integer>> map){
            int sum = 0;
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                sum+=map.get(key).size();
            }
            return sum;
        }

        public String getPrintContent(){
            StringBuffer sb = new StringBuffer();
            sb.append(start);
            for(String c:content){
                sb.append(c);
            }
            sb.append(end);
            return sb.toString();
        }


        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }
    }
}
