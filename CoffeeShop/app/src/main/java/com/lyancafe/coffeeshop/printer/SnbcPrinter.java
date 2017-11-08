package com.lyancafe.coffeeshop.printer;

import android.content.Context;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.snbc.sdk.barcode.BarInstructionImpl.BarPrinter;
import com.snbc.sdk.barcode.IBarInstruction.ILabelEdit;
import com.snbc.sdk.barcode.IBarInstruction.ILabelQuery;
import com.snbc.sdk.barcode.enumeration.InstructionType;
import com.snbc.sdk.barcode.enumeration.Rotation;
import com.snbc.sdk.connect.connectImpl.USBConnect;
import com.snbc.sdk.connect.connectImpl.WifiConnect;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/11/7.
 */

public class SnbcPrinter implements NetPrint {

    private static final String TAG = "SnbcPrinter";
    private String bigLabelIP;
    private String smallLabelIP;
    private int port;

    public SnbcPrinter(String bigLabelIP, String smallLabelIP, int port) {
        this.bigLabelIP = bigLabelIP;
        this.smallLabelIP = smallLabelIP;
        this.port = port;
    }

    @Override
    public void printSummaryInfo(List<OrderBean> orderBeanList) {

    }

    @Override
    public void printBigLabel(OrderBean orderBean) {
        LogUtil.d(TAG,"Snbc printBigLabel");
        List<PrintOrderBean> printList = Calculator.calculatePinterOrderBeanList(orderBean);

        BarPrinter printer = CSApplication.getInstance().getPrinter();
        ILabelEdit labelEdit = printer.labelEdit();
        for(PrintOrderBean bean:printList){
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
            try {
                String font = "E:SIMSUN.FNT ";
                labelEdit.setColumn(1, 16);
                labelEdit.setLabelSize(640, 400);
                labelEdit.selectPrinterCodepage(26);
                labelEdit.printText(10, 20, font,bean.getShopOrderNo(), Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(300, 20, font,bean.getLocalStr(), Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(10, 90, font,"订单编号:", Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(130, 90, font,bean.getOrderId()+OrderHelper.getWxScanStrForPrint(bean), Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(440, 90, font,OrderHelper.getPrintFlag(bean.getOrderSn()), Rotation.Rotation0, 12, 18, 0);

                labelEdit.printText(14, 140, font,cupList[0], Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(314, 140, font,cupList[1], Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(14, 190, font,cupList[2], Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(314, 190, font,cupList[3], Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(10, 250, font,"收货人:", Rotation.Rotation0, 12, 18, 0);

                labelEdit.printText(110, 250, font,bean.getReceiverName(), Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(290, 250, font,"送达时间:", Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(410, 250, font,OrderHelper.getPeriodOfExpectedtime(bean), Rotation.Rotation0, 12, 18, 0);
                labelEdit.printText(10, 290, font,"地址:", Rotation.Rotation0, 12, 18, 0);
                printer.labelControl().print(1, 1);

                LogUtil.d(TAG,"打印指令已经写入");
            }catch (Exception e){
                LogUtil.e(TAG,e.getMessage());
            }
        }
        OrderHelper.addPrintedSet(CSApplication.getInstance(), orderBean.getOrderSn());


    }

    @Override
    public void printBigLabels(List<OrderBean> orderBeanList) {

    }

    @Override
    public void printSmallLabel(OrderBean orderBean) {

    }

    @Override
    public void printSmallLabels(List<OrderBean> orderBeanList) {

    }

    @Override
    public void printTimeControlPaster(MaterialItem materialItem) {

    }

    @Override
    public void printBlankPaster() {

    }

    @Override
    public void printMaterialBigLabel(MaterialItem materialItem) {

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
                "BOX 8,110,616,220,1,10\n"+

                "TEXT 14,130,\"TSS24.BF2\",0,1,1,\""+cupList[0]+"\""+"\n"+
                "TEXT 314,130,\"TSS24.BF2\",0,1,1,\""+cupList[1]+"\""+"\n"+
                "TEXT 14,180,\"TSS24.BF2\",0,1,1,\""+cupList[2]+"\""+"\n"+
                "TEXT 314,180,\"TSS24.BF2\",0,1,1,\""+cupList[3]+"\""+"\n"+

                "TEXT 10,240,\"TSS24.BF2\",0,1,1,\"收货人:\"\n" +
                "TEXT 110,240,\"TSS24.BF2\",0,1,1,\""+bean.getReceiverName()+"\"\n" +
                "TEXT 290,240,\"TSS24.BF2\",0,1,1,\"送达时间:\"\n" +
                "TEXT 410,240,\"TSS24.BF2\",0,1,1,\""+OrderHelper.getPeriodOfExpectedtime(bean)+"\"\n" +
                "TEXT 10,280,\"TSS24.BF2\",0,1,1,\"地址:\"\n" +
                addressCMD +
                "PRINT 1,1\n";

    }

    @Override
    public String OTCForSmallLabel(PrintCupBean orderBean) {
        return null;
    }

    @Override
    public void checkPrinterStatus(String ip, int port) {

    }

    @Override
    public void writeCommand(String ip, int port, String command) {

    }

    public  void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WifiConnect wifiConnect = new WifiConnect("192.168.3.228", 9100);
                    wifiConnect.DecodeType("GB18030");
                    wifiConnect.connect();
                    BarPrinter.BarPrinterBuilder builder = new BarPrinter.BarPrinterBuilder();
                    builder.buildDeviceConnenct(wifiConnect);
                    builder.buildInstruction(InstructionType.valueOf("BPLZ"));
                    final BarPrinter printer = builder.getBarPrinter();
                    CSApplication application = CSApplication.getInstance();
                    application.setPrinter(printer);
                    application.setConnect(wifiConnect);
                    LogUtil.d(TAG, "连接第一步");
                    updateStoredCustomFontArray(application, printer, application.getApplicationContext());

           /* updateStoredBuiltinFontArray(application,printer,application.getApplicationContext());
            updateOSFontFileArray(application,printer,application.getApplicationContext());
            updateOSFormatFileArray(application,printer,application.getApplicationContext());
            updateDiskSymbol(application,printer,application.getApplicationContext());
            updateOSImageFileForPrintArray(application,printer,application.getApplicationContext());
            updateStoredCustomFontArray(application,printer,application.getApplicationContext());
            updateStoredImageArray(application,printer,application.getApplicationContext());
            updateStoredFormatArray(application,printer,application.getApplicationContext());
            updateOSImageFileArray(application,printer,application.getApplicationContext());*/

           /* if(InstructionType.valueOf(mType) != InstructionType.BPLA){
                AlertDialogUtil.showDialog("  Connect to print successful!\r\n The printer's name is "+printer.labelQuery().getPrinterName(), ConnectivityActivity.this);
            }else{
                AlertDialogUtil.showDialog("  Connect to print successful!", ConnectivityActivity.this);
            }*/
                    LogUtil.d(TAG, "连接成功");
                } catch (Exception e) {
//            e.printStackTrace();
                    LogUtil.e(TAG, "e  =" + e);
                }
            }
        }).start();

    }

    public static void updateStoredCustomFontArray(CSApplication application,BarPrinter printer,Context context){
        try {
            ILabelQuery labelQuery = printer.labelQuery();
            if(labelQuery.getPrinterLanguage() == InstructionType.BPLA && !(application.getConnect() instanceof USBConnect)){
                application.setStoredCustomFontArray(null);
                return;
            }
            Set<String> fontSet;
            fontSet = labelQuery.getFontFileName();
          /*  FontInfo[] storedBuiltinFontArray = application.getStoredBuildinFontArray();
            for (int i = 0; i < storedBuiltinFontArray.length; i++) {
                if(fontSet.contains(storedBuiltinFontArray[i].getFontName())){
                    fontSet.remove(storedBuiltinFontArray[i].getFontName());
                }
            }*/
          LogUtil.d(TAG,"fontSet = "+fontSet.size());
            application.setStoredCustomFontArray(fontSet.toArray(new String[fontSet.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,e.getMessage());
        }
    }
}
