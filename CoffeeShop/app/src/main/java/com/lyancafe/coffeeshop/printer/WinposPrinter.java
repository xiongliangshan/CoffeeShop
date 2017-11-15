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
import com.lyancafe.coffeeshop.utils.OrderIdComparator;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */

public class WinposPrinter implements NetPrint {

    private static final String TAG = "WinposPrinter";
    private String bigLabelIP;
    private String smallLabelIP;
    private int port;

    public WinposPrinter(String bigLabelIP, String smallLabelIP, int port) {
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
        List<PrintObject> printObjects = PrintObject.transformPrintObjects(coffeeMap,recipeFittingsMap);
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
        Collections.sort(orderBeanList,new OrderIdComparator());
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
        List<PrintCupBean> cupBeanList = Calculator.calculateBatchCupList(orderBeanList);
        for(PrintCupBean bean:cupBeanList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);
        }
    }

    @Override
    public void printTimeControlPaster(MaterialItem materialItem) {
        String pasterContent = "N"+"\n"+
                "OD"+"\n"+
                "q240"+"\n"+
                "Q160,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A10,20,0,230,1,1,N,\""+materialItem.getName()+"\""+"\n"+
                "A10,55,0,230,1,1,N,\""+Calculator.getOverDueDate(materialItem.getOverdueTime())+"\""+"\n"+
                "A10,80,0,230,1,1,N,\"过期\""+"\n"+
                "A10,120,0,230,1,1,N,\"原始到期:\""+"\n"+
                "A120,120,0,230,1,1,N,\"____________\""+"\n"+
                "P1"+"\n";
        writeCommand(smallLabelIP,port,pasterContent);
    }

    @Override
    public void printBlankPaster() {
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
        writeCommand(smallLabelIP,port,pasterContent);
    }

    @Override
    public void printMaterialBigLabel(MaterialItem materialItem) {
        String printMaterialContent =  "N"+"\n"+
                "OD"+"\n"+
                "q640"+"\n"+
                "Q400,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A60,150,0,230,3,4,N,\""+materialItem.getName()+"\""+"\n"+
                "P1"+"\n";
        writeCommand(bigLabelIP,port,printMaterialContent);
    }

    @Override
    public String OTCForBigLabel(PrintOrderBean bean) {
        String[] cupList = new String[]{"","","",""};
        List<PrintCupBean> coffeeList = bean.getCoffeeList();
        for(int i =0;i<coffeeList.size();i++){
            if(i>3){
                LogUtil.e(TAG,"OTCForBigLabel ，数据异常");
                break;
            }
            PrintCupBean pc = coffeeList.get(i);
            cupList[i] = pc.getCoffee()+Calculator.formatLabel(pc.getLabel());
        }

        String addressCMD, addr1,addr2,addr3;
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
                "A140,100,0,230,1,1,N,\""+bean.getOrderId()+OrderHelper.getWxScanStrForPrint(bean)+"\""+"\n"+
                "A450,100,0,230,1,1,N,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\""+"\n"+
                "A20,120,0,230,1,1,N,\"-------------------------------------------------- \""+"\n"+
                "A20,150,0,230,1,1,N,\""+cupList[0]+"\""+"\n"+
                "A320,150,0,230,1,1,N,\""+cupList[1]+"\""+"\n"+
                "A20,200,0,230,1,1,N,\""+cupList[2]+"\""+"\n"+
                "A320,200,0,230,1,1,N,\""+cupList[3]+"\""+"\n"+
                "A20,230,0,230,1,1,N,\"-------------------------------------------------- \""+"\n"+
                "A20,260,0,230,1,1,N,\"收货人 \""+"\n"+
                "A120,260,0,230,1,1,N,\""+bean.getReceiverName()+"\""+"\n"+
                "A300,260,0,230,1,1,N,\"送达时间 \""+"\n"+
                "A420,260,0,230,1,1,N,\""+OrderHelper.getPeriodOfExpectedtime(bean)+"\""+"\n"+
                "A20,300,0,230,1,1,N,\"地址 \""+"\n"+
                addressCMD +                             //配送地址
                "P1"+"\n";

    }

    @Override
    public String OTCForSmallLabel(PrintCupBean orderBean) {
        return  "N"+"\n"+
                "OD"+"\n"+
                "q240"+"\n"+
                "Q160,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A20,40,0,230,1,1,N,\""+orderBean.getShopOrderNo()+"\""+"\n"+
                "A110,40,0,230,1,1,N,\""+orderBean.getBoxAmount()+"-"+orderBean.getBoxNumber()+"|"+orderBean.getCupAmount()+"-" +orderBean.getCupNumber()+"\""+"\n"+ //杯数盒子信息
                "A20,70,0,230,1,1,N,\""+orderBean.getCoffee()+"\""+"\n"+
                "A20,100,0,230,1,1,N,\""+orderBean.getLabel()+"\""+"\n"+
                "P1"+"\n";
    }

    @Override
    public void checkPrinterStatus(String ip, int port) {
        boolean pingResult = ping(ip);
        if (pingResult) {
            ToastUtil.showToast(CSApplication.getInstance().getApplicationContext(),ip+"在线");
        } else {
            ToastUtil.showToast(CSApplication.getInstance().getApplicationContext(),ip+"无法连接");
        }

    }

    private boolean ping(String ip) {
        int timeOut = 3000; //定义超时，表明该时间内连不上即认定为不可达，超时值不能太小。
        try {//ping功能
            boolean status = InetAddress.getByName(ip).isReachable(timeOut);
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

    @Override
    public void writeCommand(String ip, int port, String command) {
        Log.d(TAG,"writeCommand,command = "+command);
        Socket client = null;
        try {
            client = new Socket(ip, port);
            Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
            writer.write(command);
            writer.flush();
            writer.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHostException:"+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException" + e.toString());
            ToastUtil.showToast(CSApplication.getInstance(),TAG+"打印机"+ip+":"+port+"无法连接");
        }finally {
            try {
                if(client!=null){
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
