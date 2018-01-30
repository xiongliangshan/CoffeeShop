package com.lyancafe.coffeeshop.printer;

import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintBeanTest;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintObject;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.bean.Product;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.OrderIdComparator;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
        Map<String,Product> productMap = new HashMap<>();
        Map<String,Map<String,Integer>> recipeFittingsMap = new HashMap<>();
        Calculator.caculateItems(orderBeanList,productMap,recipeFittingsMap);

        List<Product> coffeeList = new ArrayList<>();
        List<Product> mixtureList = new ArrayList<>();
        List<Product> drinkList = new ArrayList<>();

        Iterator<String> iterator = productMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            Product value = productMap.get(key);
            LogUtil.d(TAG,key+"-------"+value.getProduceProcess());
            if(value.getProduceProcess()==1){
                coffeeList.add(value);
            }else if(value.getProduceProcess()==2) {
                drinkList.add(value);
            }else{
                mixtureList.add(value);
            }
        }
        Collections.sort(coffeeList);
        Collections.sort(drinkList);
        Collections.sort(mixtureList);

        List<PrintObject> firstList = PrintObject.transformPrintObjects(coffeeList,recipeFittingsMap);
        List<Product> list = new ArrayList<>();
        list.addAll(drinkList);
        list.addAll(0,mixtureList);
        List<PrintObject> secondList = PrintObject.transformPrintObjects(list,recipeFittingsMap);


        if(secondList.size()>0){
            for(int i=secondList.size()-1;i>=0;i--){
                writeCommand(bigLabelIP,port,secondList.get(i).getPrintContent());
            }
        }

        if(firstList.size()>0){
            for(int i=firstList.size()-1;i>=0;i--){
                writeCommand(bigLabelIP,port,firstList.get(i).getPrintContent());
            }
        }



    }



    @Override
    public void printBigLabelTest(List<String> contents) {

        String printMaterialContent =  "N"+"\n"+
                "OD"+"\n"+
                "q640"+"\n"+
                "Q400,16"+"\n"+
                "S3"+"\n"+
                "D8"+"\n"+
                "A10,18,0,230,1,1,N,\""+contents.get(0)+"\""+"\n"+
                "A10,43,0,230,1,1,N,\""+contents.get(1)+"\""+"\n"+
                "A10,68,0,230,1,1,N,\""+contents.get(2)+"\""+"\n"+
                "A10,93,0,230,1,1,N,\""+contents.get(3)+"\""+"\n"+
                "A10,118,0,230,1,1,N,\""+contents.get(4)+"\""+"\n"+
                "A10,143,0,230,1,1,N,\""+contents.get(5)+"\""+"\n"+
                "A10,168,0,230,1,1,N,\""+contents.get(6)+"\""+"\n"+
                "A10,203,0,230,1,1,N,\""+contents.get(7)+"\""+"\n"+
                "A10,228,0,230,1,1,N,\""+contents.get(8)+"\""+"\n"+
                "A10,253,0,230,1,1,N,\""+contents.get(9)+"\""+"\n"+
                "A10,285,0,230,1,1,N,\""+contents.get(10)+"\""+"\n"+
                "A10,310,0,230,1,1,N,\""+contents.get(11)+"\""+"\n"+
                "A10,335,0,230,1,1,N,\""+contents.get(12)+"\""+"\n"+
                "A10,360,0,230,1,1,N,\""+contents.get(13)+"\""+"\n"+
                "P1"+"\n";


        writeCommand(bigLabelIP,port,printMaterialContent);
    }



    @Override
    public void printBigLabel(OrderBean orderBean) {
        List<PrintOrderBean> printList = null;
        boolean isSimplify = PrintSetting.isSimplifyEnable(CSApplication.getInstance());
        if(isSimplify){
            printList = Calculator.calculateBigLabelObjects(orderBean);
        }else{
            printList = Calculator.calculatePinterOrderBeanList(orderBean);
        }

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
        Collections.sort(cupBeanList);
        for(PrintCupBean bean:cupBeanList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);
        }
    }

    @Override
    public void printTimeControlPaster(MaterialItem materialItem) {
        boolean isNewMode = PrintSetting.isNewMaterialEnable(CSApplication.getInstance());
        String pasterContent = null;
        if(isNewMode){
            if("冻品类".equals(materialItem.getCategoryName())){
                pasterContent = "N"+"\n"+
                        "OD"+"\n"+
                        "q240"+"\n"+
                        "Q160,16"+"\n"+
                        "S3"+"\n"+
                        "D8"+"\n"+
                        "A10,20,0,230,1,1,N,\""+"品名:"+materialItem.getName()+"\""+"\n"+
                        "A10,55,0,230,1,1,N,\"解冻日期:____-__-___\""+"\n"+
                        "A10,90,0,230,1,1,N,\"使用期限:____-__-___\""+"\n"+
                        "A10,125,0,230,1,1,N,\"原始到期:____-__-___\""+"\n"+
                        "P1"+"\n";
            }else {
                pasterContent = "N"+"\n"+
                        "OD"+"\n"+
                        "q240"+"\n"+
                        "Q160,16"+"\n"+
                        "S3"+"\n"+
                        "D8"+"\n"+
                        "A10,20,0,230,1,1,N,\""+"品名:"+materialItem.getName()+"\""+"\n"+
                        "A10,55,0,230,1,1,N,\"开封日期:____-__-___\""+"\n"+
                        "A10,90,0,230,1,1,N,\"废弃日期:____-__-___\""+"\n"+
                        "A10,125,0,230,1,1,N,\"原始到期:____-__-___\""+"\n"+
                        "P1"+"\n";
            }
        }else {
            pasterContent = "N"+"\n"+
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
        }

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

        boolean isSimplify = PrintSetting.isSimplifyEnable(CSApplication.getInstance());
        if(bean.getCupAmount()==1 && isSimplify){
            String expectedTime = bean.getInstant()==1?":尽快送达":OrderHelper.getPeriodOfExpectedtime(bean);
            String address = bean.getAddress();
            String bestAddress = "";
            if(!TextUtils.isEmpty(address)&& address.length()>25){
                int startIndex = address.indexOf("市");
                LogUtil.d(TAG,"startIndex = "+startIndex);
                if(startIndex!=-1){
                    bestAddress = address.substring(startIndex+1);
                }else {
                    bestAddress = address;
                }
            }else {
                bestAddress = address;
            }
            return  "N"+"\n"+
                    "q640"+"\n"+
                    "Q400,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n"+
                    "A20,20,0,230,2,2,N,\""+Calculator.getCheckShopNo(bean)+"\""+"\n"+
                    "A280,20,0,230,2,2,N,\""+bean.getLocalStr()+"\""+"\n"+           //杯数盒子信息
                    "A20,70,0,230,1,1,N,\"单号:\""+"\n"+ //订单编号
                    "A70,70,0,230,1,1,N,\""+bean.getOrderId()+OrderHelper.getWxScanStrForPrint(bean)+","+"\""+"\n"+
                    "A180,70,0,230,1,1,N,\""+bean.getReceiverName()+","+"\""+"\n"+
                    "A320,70,0,230,1,1,N,\"送达时间\""+"\n"+
                    "A420,70,0,230,1,1,N,\""+expectedTime+"\""+"\n"+
                    "A570,70,0,230,1,1,N,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\""+"\n"+
                    "A20,100,0,230,1,1,N,\""+bestAddress+"\""+"\n"+
                    "A20,130,0,230,1,1,N,\""+cupList[0]+"\""+"\n"+
                    "P1"+"\n";
        }else {
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
                    "A20,30,0,230,2,2,N,\""+Calculator.getCheckShopNo(bean)+"\""+"\n"+
                    "A280,30,0,230,2,2,N,\""+bean.getLocalStr()+"\""+"\n"+           //杯数盒子信息
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



    }

    @Override
    public String OTCForSmallLabel(PrintCupBean orderBean) {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        boolean needPrintTime =  user.isNeedPrintTime();
        String drinkGuide = user.getDrinkGuide();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
        if(needPrintTime){
            return  "N"+"\n"+
                    "OD"+"\n"+
                    "q240"+"\n"+
                    "Q160,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n"+
                    "A18,16,0,230,1,1,N,\""+orderBean.getShopOrderNo()+"\""+"\n"+
                    "A18,43,0,230,1,1,N,\""+orderBean.getCoffee()+"\""+"\n"+
                    "A18,68,0,230,1,1,N,\""+orderBean.getLabel()+"\""+"\n"+
                    "A18,101,0,230,1,1,N,\""+time+"\""+"\n"+
                    "A18,126,0,230,1,1,N,\""+drinkGuide+"\""+"\n"+
                    "P1"+"\n";
        }else {
            return  "N"+"\n"+
                    "OD"+"\n"+
                    "q240"+"\n"+
                    "Q160,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n"+
                    "A20,40,0,230,1,1,N,\""+orderBean.getShopOrderNo()+"\""+"\n"+
//                "A110,40,0,230,1,1,N,\""+orderBean.getBoxAmount()+"-"+orderBean.getBoxNumber()+"|"+orderBean.getCupAmount()+"-" +orderBean.getCupNumber()+"\""+"\n"+ //杯数盒子信息
                    "A20,70,0,230,1,1,N,\""+orderBean.getCoffee()+"\""+"\n"+
                    "A20,100,0,230,1,1,N,\""+orderBean.getLabel()+"\""+"\n"+
                    "P1"+"\n";
        }
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
//        Log.d(TAG,"writeCommand,command = "+command);
        Socket client = null;
        try {
            client = new Socket(ip, port);
            Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
            writer.write(command);
            writer.flush();
            writer.close();
            Thread.sleep(300);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHostException:"+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException" + e.toString());
            ToastUtil.showToast(CSApplication.getInstance(),TAG+"打印机"+ip+":"+port+"无法连接");
            Logger.getLogger().error("打印机 winpos "+ip+":"+port+" 无法连接");
        }catch (InterruptedException e){
            Log.e(TAG, "InterruptedException" + e.getMessage());
        }finally{
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
