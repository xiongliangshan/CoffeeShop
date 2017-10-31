package com.lyancafe.coffeeshop.printer;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.Api;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/27.
 */

public class PrintFace {

    private static final String TAG = "PrintFace";

    private NetPrint mPrinter = null;

    private static PrintFace mInst;

    private static String BIGLABELIP = "192.19.1.231";
    private static String SMALLLABELIP = "192.19.1.232";
    private static final int PORT = 9100;

    private ThreadPoolExecutor mPoolExecutor;

    private PrintFace() {
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if(Api.BASE_URL.contains("cn")||Api.BASE_URL.contains("192.168")||"测试-滴水湖".equals(LoginHelper.getUser(CSApplication.getInstance()).getShopName())){
            BIGLABELIP = "192.168.0.229";
            SMALLLABELIP = "192.168.0.229";
        }else{
            BIGLABELIP = "192.19.1.231";
            SMALLLABELIP = "192.19.1.232";
        }
        mPrinter = new FujitsuPrinter(BIGLABELIP,SMALLLABELIP,PORT);
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }



    public static PrintFace getInst(){
        if(mInst==null){
            mInst = new PrintFace();
        }

        return mInst;

    }


    /**
     * 同时打印一个订单的大小标签
     */
    public void startPrintWholeOrderTask(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printBigLabel(order);
            }
        });
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printSmallLabel(order);
            }
        });
    }

    /**
     * 批量生产打印
     */
    public void printBatch(final List<OrderBean> batchOrders){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //打印汇总信息
                mPrinter.printSummaryInfo(batchOrders);

                //打印大标签
                mPrinter.printBigLabels(batchOrders);
            }
        });

        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //打印小标签
                mPrinter.printSmallLabels(batchOrders);
            }
        });

    }

    /**
     * 仅打印盒贴标签
     * @param order
     */
    public void startPrintOnlyBoxTask(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printBigLabel(order);
            }
        });
    }

    /**
     * 仅打印杯贴标签
     * @param order
     */
    public void startPrintOnlyCupTask(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printSmallLabel(order);
            }
        });

    }

    /**
     * 启动打印物料任务
     * @param materialItem
     */
    public void startPrintMaterialTask(final MaterialItem materialItem){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printMaterialBigLabel(materialItem);
            }
        });
    }

    /**
     * 启动打印时控贴任务
     * @param materialItem
     */
    public void startPrintPasterTask(final MaterialItem materialItem){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printTimeControlPaster(materialItem);
            }
        });
    }

    /**
     * 启动打印空白时控贴任务
     */
    public void startPrintBlankPasterTask(){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printBlankPaster();
            }
        });
    }

    //检查打印机状态
    public void checkPrinterStatus() {
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.checkPrinterStatus(BIGLABELIP,PORT);
            }
        });
    }

    //打印测试
    public void printTest(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.printBigLabel(order);
            }
        });
    }

}
