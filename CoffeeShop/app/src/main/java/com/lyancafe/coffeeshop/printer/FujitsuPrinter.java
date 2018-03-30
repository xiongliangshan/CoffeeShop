package com.lyancafe.coffeeshop.printer;

import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.bean.Product;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.OrderIdComparator;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

        List<PrintSummaryObject> firstList = PrintSummaryObject.transformPrintObjects(coffeeList,recipeFittingsMap);
        List<Product> list = new ArrayList<>();
        list.addAll(drinkList);
        list.addAll(0,mixtureList);
        List<PrintSummaryObject> secondList = PrintSummaryObject.transformPrintObjects(list,recipeFittingsMap);


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
    public void printBigLabel(OrderBean orderBean) {
        List<PrintOrderBean> printList = null;
        boolean isSimplify = PrintSetting.isSimplifyEnable(CSApplication.getInstance());
        LogUtil.d(TAG,"isSimplify = "+isSimplify);
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
        LogUtil.d(TAG,"printSmallLabels");
        List<PrintCupBean> cupBeanList = Calculator.calculateBatchCupList(orderBeanList);
        Collections.sort(cupBeanList);
        for(PrintCupBean bean:cupBeanList){
            String printCommand = OTCForSmallLabel(bean);
            writeCommand(smallLabelIP,port,printCommand);
        }
    }

    @Override
    public void printTimeControlPaster(MaterialItem materialItem) {
        boolean isPrintSecond = PrintSetting.isPrintSecond(CSApplication.getInstance());
        boolean isPrintTime = PrintSetting.isPrintTime(CSApplication.getInstance());
        String pasterContent = null;
        if(isPrintTime){
            if ("冻品类".equals(materialItem.getCategoryName())) {
                if (isPrintSecond) {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + " \"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"解冻:" + Calculator.getCurrentDate(isPrintSecond) + "\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"期限:" + Calculator.getOverDueDate(materialItem.getOverdueTime(), isPrintSecond) + "\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                } else {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + " \"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"解冻日期:" + Calculator.getCurrentDate(isPrintSecond) + "\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"使用期限:" + Calculator.getOverDueDate(materialItem.getOverdueTime(), isPrintSecond) + "\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                }
            } else {
                if (isPrintSecond) {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + "\"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"开封:" + Calculator.getCurrentDate(isPrintSecond) + "\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"期限:" + Calculator.getOverDueDate(materialItem.getOverdueTime(), isPrintSecond) + "\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                } else {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + "\"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"开封日期:" + Calculator.getCurrentDate(isPrintSecond) + "\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"使用期限:" + Calculator.getOverDueDate(materialItem.getOverdueTime(), isPrintSecond) + "\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                }
            }
        } else {
            if ("冻品类".equals(materialItem.getCategoryName())) {
                if (isPrintSecond) {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + " \"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"解冻:___-__-__|__:__\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"期限:___-__-__|__:__\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                } else {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + " \"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"解冻日期:____-__-___\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"使用期限:____-__-___\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                }
            } else {
                if (isPrintSecond) {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + "\"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"开封:___-__-__|__:__\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"期限:___-__-__|__:__\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                } else {
                    pasterContent =
                            "SIZE 30 mm, 20 mm\n" +
                                    "GAP 3 mm, 0 mm\n" +
                                    "SET RIBBON OFF\n" +
                                    "DIRECTION 1,0\n" +
                                    "CLS\n" +
                                    "TEXT 3,20,\"TSS24.BF2\",0,1,1,\"品名:" + materialItem.getName() + "\"\n" +
                                    "TEXT 3,55,\"TSS24.BF2\",0,1,1,\"开封日期:____-__-___\"\n" +
                                    "TEXT 3,90,\"TSS24.BF2\",0,1,1,\"使用期限:____-__-___\"\n" +
                                    "TEXT 3,125,\"TSS24.BF2\",0,1,1,\"原始到期:____-__-___\"\n" +
                                    "PRINT 1,1\n";
                }
            }
        }
        writeCommand(smallLabelIP, port, pasterContent);
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
    public void printCancellationPaster(){
        String printTestContent =
                    "SIZE 80 mm, 49 mm\n" +
                            "GAP 2 mm, 0 mm\n" +
                            "SET RIBBON OFF\n" +
                            "DIRECTION 1,0\n" +
                            "CLS\n" +
                            "TEXT 130,150,\"TSS32.BF2\",0,3,3,\""+ "退货勿用" +"\"\n" +
                            "PRINT 1,1\n";
        writeCommand(bigLabelIP, port, printTestContent);
    }

    @Override
    public void printBigLabelTest(List<String> contents) {

        String printTestContent =
                "SIZE 80 mm, 49 mm\n" +
                "GAP 2 mm, 0 mm\n" +
                "SET RIBBON OFF\n" +
                "DIRECTION 1,0\n" +
                "CLS\n" +
                "TEXT 8,10,\"TSS24.BF2\",0,1,1,\"" + contents.get(0) + "\"\n" +
                "TEXT 8,35,\"TSS24.BF2\",0,1,1,\"" + contents.get(1) + "\"\n" +
                "TEXT 8,60,\"TSS24.BF2\",0,1,1,\"" + contents.get(2) + "\"\n" +
                "TEXT 8,85,\"TSS24.BF2\",0,1,1,\"" + contents.get(3) + "\"\n" +
                "TEXT 8,110,\"TSS24.BF2\",0,1,1,\"" + contents.get(4) + "\"\n" +
                "TEXT 8,135,\"TSS24.BF2\",0,1,1,\"" + contents.get(5) + "\"\n" +
                "TEXT 8,160,\"TSS24.BF2\",0,1,1,\"" + contents.get(6) + "\"\n" +

                "TEXT 8,196,\"TSS24.BF2\",0,1,1,\"" + contents.get(7) + "\"\n" +
                "TEXT 8,221,\"TSS24.BF2\",0,1,1,\"" + contents.get(8) + "\"\n" +
                "TEXT 8,246,\"TSS24.BF2\",0,1,1,\"" + contents.get(9) + "\"\n" +

                "TEXT 8,282,\"TSS24.BF2\",0,1,1,\"" + contents.get(10) + "\"\n" +
                "TEXT 8,307,\"TSS24.BF2\",0,1,1,\"" + contents.get(11) + "\"\n" +
                "TEXT 8,332,\"TSS24.BF2\",0,1,1,\"" + contents.get(12) + "\"\n" +
                "TEXT 8,357,\"TSS24.BF2\",0,1,1,\"" + contents.get(13) + "\"\n" +
                "PRINT 1,1\n";

        writeCommand(bigLabelIP, port, printTestContent);
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
        String[] cupList = new String[]{"","","",""};
        List<PrintCupBean> coffeeList = bean.getCoffeeList();
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        String arriveTime = "";
        if (user.isOpenFulfill()) {
            arriveTime = OrderHelper.getFormatTimeToStr(bean.getInstanceTime());
        } else {
            arriveTime = OrderHelper.getFormatTimeToStr(bean.getExpectedTime());
        }
//start 老版本
//        for(int i =0;i<coffeeList.size();i++){
//            if(i>5){
//                LogUtil.e(TAG,"OTCForBigLabel ，数据异常");
//                break;
//            }
//            PrintCupBean pc = coffeeList.get(i);
//            cupList[i] = pc.getCoffee()+Calculator.formatLabel(pc.getLabel());
//        }
        //end 老版本
        //start 新版本
        String labels = ""; // 产品
        String coffee = "";//咖啡
        int i = 0;
        System.out.println("coffeeList="+coffeeList);
        for (; i < coffeeList.size(); i++) {
            if (i > 4) {
                LogUtil.e(TAG, "OTCForBigLabel ，数据异常");
                break;
            }
            PrintCupBean pc = coffeeList.get(i);
            System.out.println("coffee="+coffee);
            if (i < 2 ){
                coffee += pc.getCoffee();
                coffee += "  ";
            } else if (i == 2){
                cupList[0] = coffee;
                coffee = "";
                coffee += pc.getCoffee();
                coffee += "  ";
            } else {
                coffee += pc.getCoffee();
                coffee += "  ";
            }
            labels += pc.getLabel() + ",";
        }
        System.out.println("coffee="+coffee);
        if(i < 2){
            cupList[0] = coffee;
        } else {
            cupList[1] = coffee;
        }
        System.out.println("1111"+labels);
        //判断是不是补单
        if(labels.contains("*")){
            String[] labelSplit = labels.split(",");
            String labelAndCup = "";
            int j = 0;
            for (String label : labelSplit){
                if (i < 2) {
                    if (j == 0) {
                        labelAndCup = "(" + label + ",";
                    } else if (j == 2) {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[1] = labelAndCup;
                        labelAndCup = "(" + label + ",";
                    } else {
                        labelAndCup += label + ",";
                    }
                } else {
                    if (j == 0) {
                        labelAndCup = "(" + label + ",";
                    } else if (j == 2) {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[2] = labelAndCup;
                        labelAndCup = "(" + label + ",";
                    } else {
                        labelAndCup += label + ",";
                    }
                }
                j++;
            }
            if(labelAndCup.length() > 0) {
                if (i < 2) {
                    if (j > 2) {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[2] = labelAndCup;
                    } else {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[1] = labelAndCup;
                    }
                } else {
                    if (j > 2) {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[3] = labelAndCup;
                    } else {
                        labelAndCup = labelAndCup.substring(0,labelAndCup.length()-1);
                        labelAndCup += ")";
                        cupList[2] = labelAndCup;
                    }
                }
            }
        } else {
            String[] labelSplit = labels.split(",");
            Map<String, Integer> labelAndCup = new HashMap<>();
            for (String label : labelSplit) {
                System.out.println("1111"+label);
                if (labelAndCup.containsKey(label)) {
                    int cup = labelAndCup.get(label) +1 ;
                    labelAndCup.put(label , cup);
                } else {
                    if (!"".equals(label)) {
                        labelAndCup.put(label, 1);
                    }
                }
            }
            int j =0;
            String labelStr = "";
            if(i >= 2){
                for(String s : labelAndCup.keySet()){
                    if (j == 0) {
                        labelStr = "(" + s + "*"+ labelAndCup.get(s) + ",";
                    } else if (j == 2){
                        labelStr = labelStr.substring(0,labelStr.length()-1);
                        labelStr += ")";
                        cupList[2] = labelStr;
                        labelStr = "(" + s + "*"+ labelAndCup.get(s)+",";
                    } else {
                        labelStr += s + "*"+ labelAndCup.get(s)+",";
                    }
                    j ++ ;
                }
                if(labelStr.length() > 0) {
                    if(j < 2){
                        labelStr = labelStr.substring(0,labelStr.length()-1);
                        labelStr += ")";
                        cupList[2] = labelStr;
                    } else {
                        labelStr = labelStr.substring(0,labelStr.length()-1);
                        labelStr += ")";
                        cupList[3] = labelStr;
                    }
                }
            } else {
                for(String s : labelAndCup.keySet()){
                    if (j == 0) {
                        labelStr = "(" + s + "*"+ labelAndCup.get(s) +",";
                    } else if (j == 2){
                        labelStr = labelStr.substring(0,labelStr.length()-1);
                        labelStr += ")";
                        cupList[1] = labelStr;
                        labelStr = "(" + s + "*" + labelAndCup.get(s) + ",";
                    } else {
                        labelStr += s + "*" + labelAndCup.get(s) + ",";
                    }
                    j++;
                }
                if(labelStr.length() > 0) {
                    if (j < 2) {
                        labelStr = labelStr.substring(0, labelStr.length() - 1);
                        labelStr += ")";
                        cupList[1] = labelStr;
                    } else {
                        labelStr = labelStr.substring(0, labelStr.length() - 1);
                        labelStr += ")";
                        cupList[2] = labelStr;
                    }
                }
            }
        }
        //end 新版本
        boolean isSimplify = PrintSetting.isSimplifyEnable(CSApplication.getInstance());
        if(bean.getCupAmount()==1 && isSimplify){
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
            return  "SIZE 80 mm, 49 mm\n" +
                    "GAP 2 mm, 0 mm\n" +
                    "SET RIBBON OFF\n" +
                    "DIRECTION 1,0\n" +
                    "CLS\n" +
                    "TEXT 10,10,\"TSS24.BF2\",0,2,2,\""+Calculator.getCheckShopNo(bean)+bean.getLocalStr()+"\""+"\n"+
                    "TEXT 540,10,\"TSS24.BF2\",0,2,2,\""+bean.getCupStr()+"\""+"\n"+           //杯数盒子信息
                    "TEXT 10,60,\"TSS24.BF2\",0,1,1,\"单号:\""+"\n"+ //订单编号
                    "TEXT 70,60,\"TSS24.BF2\",0,1,1,\""+bean.getOrderHashId()+","+"\""+"\n"+
                    "TEXT 180,60,\"TSS24.BF2\",0,1,1,\""+bean.getReceiverName()+","+"\""+"\n"+
                    "TEXT 320,60,\"TSS24.BF2\",0,1,1,\"送达时间\""+"\n"+
                    "TEXT 420,60,\"TSS24.BF2\",0,1,1,\""+arriveTime+"\""+"\n"+
                    "TEXT 570,60,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\""+"\n"+
                    "TEXT 10,90,\"TSS24.BF2\",0,1,1,\""+bestAddress+"\""+"\n"+
                    "TEXT 10,120,\"TSS24.BF2\",0,1,1,\""+cupList[0]+"\""+"\n"+
                    "TEXT 10,150,\"TSS24.BF2\",0,1,1,\""+cupList[1]+"\""+"\n"+
                    "TEXT 10,180,\"TSS24.BF2\",0,1,1,\""+cupList[2]+"\""+"\n"+
                    "TEXT 10,210,\"TSS24.BF2\",0,1,1,\""+cupList[3]+"\""+"\n"+
                    "PRINT 1,1\n";
        }else {
            String addressCMD, addr1,addr2,addr3;
            int length = bean.getAddress().length();
            if (length <= 22) {
                addressCMD = "TEXT 90,295,\"TSS24.BF2\",0,1,1,\""+bean.getAddress()+"\""+"\n";
            } else if(length>22 && length<43){
                addr1 = bean.getAddress().substring(0, 22);
                addr2 = bean.getAddress().substring(22);
                addressCMD = "TEXT 90,295,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
                        "TEXT 90,325,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n";
            }else{
                addr1 = bean.getAddress().substring(0, 22);
                addr2 = bean.getAddress().substring(22,43);
                addr3 = bean.getAddress().substring(43);
                addressCMD = "TEXT 90,295,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
                        "TEXT 90,325,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n"+
                        "TEXT 90,355,\"TSS24.BF2\",0,1,1,\""+addr3+"\""+"\n";
            }

            return  "SIZE 80 mm, 49 mm\n" +
                    "GAP 2 mm, 0 mm\n" +
                    "SET RIBBON OFF\n" +
                    "DIRECTION 1,0\n" +
                    "CLS\n" +
                    "TEXT 10,10,\"TSS24.BF2\",0,2,2,\""+Calculator.getCheckShopNo(bean)+bean.getLocalStr()+"\""+"\n"+
                    "TEXT 540,10,\"TSS24.BF2\",0,2,2,\""+bean.getCupStr()+"\""+"\n"+           //杯数盒子信息
                    "TEXT 10,80,\"TSS24.BF2\",0,1,1,\"订单编号:\"\n" +
                    "TEXT 130,80,\"TSS24.BF2\",0,1,1,\""+bean.getOrderHashId()+"\"\n" +
                    "TEXT 440,80,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\"\n" +
                    "BOX 8,110,616,250,1,10\n"+

                    "TEXT 14,120,\"TSS24.BF2\",0,1,1,\""+cupList[0]+"\""+"\n"+
                    "TEXT 14,150,\"TSS24.BF2\",0,1,1,\""+cupList[1]+"\""+"\n"+
                    "TEXT 14,180,\"TSS24.BF2\",0,1,1,\""+cupList[2]+"\""+"\n"+
                    "TEXT 14,210,\"TSS24.BF2\",0,1,1,\""+cupList[3]+"\""+"\n"+

                    "TEXT 10,260,\"TSS24.BF2\",0,1,1,\"收货人:\"\n" +
                    "TEXT 110,260,\"TSS24.BF2\",0,1,1,\""+bean.getReceiverName()+"\"\n" +
                    "TEXT 290,260,\"TSS24.BF2\",0,1,1,\"送达时间:\"\n" +
                    "TEXT 410,260,\"TSS24.BF2\",0,1,1,\""+arriveTime+"\"\n" +
                    "TEXT 10,295,\"TSS24.BF2\",0,1,1,\"地址:\"\n" +
                     addressCMD +
                    "PRINT 1,1\n";
            //老版本备份
//            String addressCMD, addr1,addr2,addr3;
//            int length = bean.getAddress().length();
//            if (length <= 22) {
//                addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+bean.getAddress()+"\""+"\n";
//            } else if(length>22 && length<43){
//                addr1 = bean.getAddress().substring(0, 22);
//                addr2 = bean.getAddress().substring(22);
//                addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
//                        "TEXT 90,310,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n";
//            }else{
//                addr1 = bean.getAddress().substring(0, 22);
//                addr2 = bean.getAddress().substring(22,43);
//                addr3 = bean.getAddress().substring(43);
//                addressCMD = "TEXT 90,280,\"TSS24.BF2\",0,1,1,\""+addr1+"\""+"\n" +
//                        "TEXT 90,310,\"TSS24.BF2\",0,1,1,\""+addr2+"\""+"\n"+
//                        "TEXT 90,340,\"TSS24.BF2\",0,1,1,\""+addr3+"\""+"\n";
//            }
//            return  "SIZE 80 mm, 49 mm\n" +
//                    "GAP 2 mm, 0 mm\n" +
//                    "SET RIBBON OFF\n" +
//                    "DIRECTION 1,0\n" +
//                    "CLS\n" +
//                    "TEXT 10,10,\"TSS24.BF2\",0,2,2,\""+Calculator.getCheckShopNo(bean)+bean.getLocalStr()+"\""+"\n"+
//                    "TEXT 540,10,\"TSS24.BF2\",0,2,2,\""+bean.getCupStr()+"\""+"\n"+           //杯数盒子信息
//                    "TEXT 10,80,\"TSS24.BF2\",0,1,1,\"订单编号:\"\n" +
//                    "TEXT 130,80,\"TSS24.BF2\",0,1,1,\""+bean.getOrderHashId()+"\"\n" +
//                    "TEXT 440,80,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPrintFlag(bean.getOrderSn())+"\"\n" +
//                    "BOX 8,110,616,220,1,10\n"+
//
//                    "TEXT 14,130,\"TSS24.BF2\",0,1,1,\""+cupList[0]+"\""+"\n"+
//                    "TEXT 314,130,\"TSS24.BF2\",0,1,1,\""+cupList[1]+"\""+"\n"+
//                    "TEXT 14,180,\"TSS24.BF2\",0,1,1,\""+cupList[2]+"\""+"\n"+
//                    "TEXT 314,180,\"TSS24.BF2\",0,1,1,\""+cupList[3]+"\""+"\n"+
//
//                    "TEXT 10,240,\"TSS24.BF2\",0,1,1,\"收货人:\"\n" +
//                    "TEXT 110,240,\"TSS24.BF2\",0,1,1,\""+bean.getReceiverName()+"\"\n" +
//                    "TEXT 290,240,\"TSS24.BF2\",0,1,1,\"送达时间:\"\n" +
//                    "TEXT 410,240,\"TSS24.BF2\",0,1,1,\""+arriveTime+"\"\n" +
//                    "TEXT 10,280,\"TSS24.BF2\",0,1,1,\"地址:\"\n" +
//                    addressCMD +
//                    "PRINT 1,1\n";

        }

    }

    public String OTCForQrcode(){
        return
        "SIZE 80 mm, 49 mm\n" +
        "GAP 2 mm, 0 mm\n" +
        "SET RIBBON OFF\n" +
        "DIRECTION 1,0\n" +
        "REFERENCE 0,0\n" +
        "BOX 10,10,610,386,3,10\n" +
        "QRCODE 400,60,H,5,A,0,M2,S6,\"http://m.lyancoffee.com/wechat/mainbuy/enter\"  \n" +
        "PRINT 1, 1\n";

    }

    @Override
    public String OTCForSmallLabel(PrintCupBean bean) {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        boolean needPrintTime =  user.isNeedPrintTime();
        String drinkGuide = user.getDrinkGuide();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
        if(needPrintTime){
            return  "SIZE 30 mm, 20 mm\n" +
                    "GAP 3 mm, 0 mm\n" +
                    "SET RIBBON OFF\n" +
                    "DIRECTION 1,0\n" +
                    "CLS\n" +
                    "TEXT 12,16,\"TSS24.BF2\",0,1,1,\""+ bean.getShopOrderNo() +"\"\n" +
                    "TEXT 12,43,\"TSS24.BF2\",0,1,1,\""+bean.getCoffee()+"\"\n" +
                    "TEXT 12,101,\"TSS24.BF2\",0,1,1,\""+time+"\""+"\n"+
                    "TEXT 12,126,\"TSS24.BF2\",0,1,1,\""+drinkGuide+"\""+"\n"+
                    "PRINT 1,1\n";

        }else {
            return  "SIZE 30 mm, 20 mm\n" +
                    "GAP 3 mm, 0 mm\n" +
                    "SET RIBBON OFF\n" +
                    "DIRECTION 1,0\n" +
                    "CLS\n" +
                    "TEXT 20,40,\"TSS24.BF2\",0,1,1,\""+ bean.getShopOrderNo() +"\"\n" +
                    "TEXT 20,70,\"TSS24.BF2\",0,1,1,\""+bean.getCoffee()+"\"\n" +
                    "PRINT 1,1\n";
        }
//        if(needPrintTime){
//            return  "SIZE 30 mm, 20 mm\n" +
//                    "GAP 3 mm, 0 mm\n" +
//                    "SET RIBBON OFF\n" +
//                    "DIRECTION 1,0\n" +
//                    "CLS\n" +
//                    "TEXT 12,16,\"TSS24.BF2\",0,1,1,\""+ bean.getShopOrderNo() +"\"\n" +
//                    "TEXT 12,43,\"TSS24.BF2\",0,1,1,\""+bean.getCoffee()+"\"\n" +
//                    "TEXT 12,68,\"TSS24.BF2\",0,1,1,\""+bean.getLabel()+"\"\n" +
//                    "TEXT 12,101,\"TSS24.BF2\",0,1,1,\""+time+"\""+"\n"+
//                    "TEXT 12,126,\"TSS24.BF2\",0,1,1,\""+drinkGuide+"\""+"\n"+
//                    "PRINT 1,1\n";
//
//        }else {
//            return  "SIZE 30 mm, 20 mm\n" +
//                    "GAP 3 mm, 0 mm\n" +
//                    "SET RIBBON OFF\n" +
//                    "DIRECTION 1,0\n" +
//                    "CLS\n" +
//                    "TEXT 20,40,\"TSS24.BF2\",0,1,1,\""+ bean.getShopOrderNo() +"\"\n" +
//                    "TEXT 20,70,\"TSS24.BF2\",0,1,1,\""+bean.getCoffee()+"\"\n" +
//                    "TEXT 20,100,\"TSS24.BF2\",0,1,1,\""+bean.getLabel()+"\"\n" +
//                    "PRINT 1,1\n";
//        }

    }

    public void printQRcode(){
        String command = OTCForQrcode();
        writeCommand(bigLabelIP,port,command);
    }

    @Override
    public void checkPrinterStatus(String ip, int port) {
        LogUtil.d(TAG,"checkPrinterStatus");
        byte[] bytes = new byte[]{0x1b,0x21,0x3f};
        byte[] result = new byte[1];
        Socket client = null;
        try {
            client = new Socket(ip, port);
            OutputStream os = client.getOutputStream();
            os.write(bytes);

            InputStream is = client.getInputStream();
            is.read(result);
            is.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHostException:"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException" + e.getMessage());
            ToastUtil.showToast(CSApplication.getInstance(),TAG+"打印机"+ip+":"+port+"无法连接");
        }finally {

            try {if(client!=null){
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG,"checkPrinterStatus: result = "+bytesToHexString(result));
        showPrompt(ip,bytesToHexString(result));
    }

    public  String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 00000000  正常         00
     * 00000001  纸仓打开      01
     * 00000010  纸张错误      02
     * 00000100  缺纸         04
     * 00001000  碳带错误      08
     * 00010000  暂停         10
     * 00100000  正在打印      20
     * 01000000  未定义        40
     * 10000000  处于学习状态   80
     * @param hexCode
     */
    private void showPrompt(String ip,String hexCode){
        String prompt = "正常";
        if("00".equals(hexCode)){
            prompt = "正常";
        }else if("01".equals(hexCode)){
            prompt = "纸仓打开";
        }else if("02".equals(hexCode)){
            prompt = "纸张错误";
        }else if("04".equals(hexCode)){
            prompt = "缺纸";
        }else if("08".equals(hexCode)){
            prompt = "碳带错误";
        }else if("10".equals(hexCode)){
            prompt = "暂停";
        }else if("20".equals(hexCode)){
            prompt = "正在打印";
        }else if("40".equals(hexCode)){
            prompt = "未定义";
        }else if("80".equals(hexCode)){
            prompt = "处于学习状态";
        }
        ToastUtil.showToast(CSApplication.getInstance().getApplicationContext(),"打印机 "+ip+" "+prompt);
    }


    @Override
    public void writeCommand(String ip, int port, String command) {
        Log.d(TAG,Thread.currentThread().getName()+" writeCommand,command = \n"+command);
        Socket client=null;
        try {
            client = new Socket(ip, port);
            client.setSoTimeout(3000);
            Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
            writer.write(command);
            writer.flush();
            writer.close();
            Thread.sleep(300);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHostException:"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException" + e.getMessage());
            ToastUtil.showToast(CSApplication.getInstance(),TAG+"打印机"+ip+":"+port+"无法连接");
            Logger.getLogger().error("打印机 富士通 "+ip+":"+port+" 无法连接");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "InterruptedException" + e.getMessage());
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
                    "DIRECTION 1,0\n" +
                    "CLS\n";

            this.end = "PRINT 1,1\n";
            this.content = new ArrayList<>();
        }


        public static List<PrintSummaryObject> transformPrintObjects(List<Product> list, Map<String,Map<String,Integer>> recipeFittingsMap ){
            List<PrintSummaryObject> printObjects = new ArrayList<>();
            if(list.size()==0){
                return printObjects;
            }

            int coffeeSize = list.size();
            int fittingSize = getMapSize(recipeFittingsMap);
            int totalSize = coffeeSize+fittingSize;
            int size = totalSize%6==0?totalSize/6:totalSize/6+1;
            LogUtil.d(TAG,"transformPrintObjects: coffeeSize ="+coffeeSize+"|fittingSize ="+fittingSize+"|totalSize="+totalSize+"|size ="+size);
            for(int i=0;i<size;i++){
                printObjects.add(new PrintSummaryObject());
            }
            int l = 30;
            int t = 30;
            int i = 0;
            for(Product product:list){
                String name = product.getName();
                int count = product.getCount();
                printObjects.get(i/6).getContent().add("TEXT "+l+","+t+",\"TSS24.BF2\",0,2,2,\""+name+" x "+count+"\"\n");
                t+=60;
                i++;
                if(i%6==0){
                    l = 30;
                    t = 30;
                }

                Map<String ,Integer> fittingsMap = recipeFittingsMap.get(name);
                if(fittingsMap!=null){
                    Iterator<String> iterator1 = fittingsMap.keySet().iterator();
                    while (iterator1.hasNext()) {
                        String fitting = iterator1.next();
                        printObjects.get(i / 6).getContent().add("TEXT " + (l + 150) + "," + t + ",\"TSS24.BF2\",0,1,1,\"" + fitting + " x " + fittingsMap.get(fitting) + "\"\n");
                        t += 60;
                        i++;
                        if (i % 6 == 0) {
                            l = 30;
                            t = 30;
                        }
                    }
                }
            }

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
            StringBuilder sb = new StringBuilder();
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
