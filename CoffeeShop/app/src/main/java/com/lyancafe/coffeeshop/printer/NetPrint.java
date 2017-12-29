package com.lyancafe.coffeeshop.printer;

import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.PrintCupBean;
import com.lyancafe.coffeeshop.bean.PrintOrderBean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public interface NetPrint {


    //打印一组订单信息汇总
    void printSummaryInfo(List<OrderBean> orderBeanList);

    //打印一个订单的大标签
    void printBigLabel(OrderBean orderBean);

    //打印一组订单的大标签
    void printBigLabels(List<OrderBean> orderBeanList);

    //打印一个订单的小标签
    void printSmallLabel(OrderBean orderBean);

    //打印一组订单的小标签
    void printSmallLabels(List<OrderBean> orderBeanList);

    //打印时控贴
    void printTimeControlPaster(MaterialItem materialItem);

    //打印空白时控贴
    void printBlankPaster();

    //打印物料大标签
    void printMaterialBigLabel(MaterialItem materialItem);


    //订单对象信息转成大标签指令
    String OTCForBigLabel(PrintOrderBean orderBean);

    //订单对象信息转成小标签指令
    String OTCForSmallLabel(PrintCupBean orderBean);


    //检查打印机状态
    void checkPrinterStatus(String ip,int port);

    //往打印机中写入命令
    void writeCommand(String ip,int port,String command);


    void printBigLabelTest(List<String> contents);
}
