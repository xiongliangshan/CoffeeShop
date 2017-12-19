package com.lyancafe.coffeeshop.printer;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.Api;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/27.
 */

public class PrintFace {

    private static final String TAG = "PrintFace";

    private NetPrint mBigPrinter = null;
    private NetPrint mSmallPrinter = null;

    private static PrintFace mInst;

    public static String BIGLABELIP = "192.19.1.231";
    public static String SMALLLABELIP = "192.19.1.232";
    private static final int PORT = 9100;

    private ThreadPoolExecutor mPoolExecutor;

    private PrintFace() {
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if(Api.BASE_URL.contains("cn")||Api.BASE_URL.contains("192.168")||"测试-滴水湖".equals(LoginHelper.getUser(CSApplication.getInstance()).getShopName())){
            BIGLABELIP = "192.168.1.228";
            SMALLLABELIP = "192.168.1.229";
        }else{
            BIGLABELIP = "192.19.1.231";
            SMALLLABELIP = "192.19.1.232";
        }
        mBigPrinter = new WinposPrinter(BIGLABELIP,SMALLLABELIP,PORT);
        mPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }



    public static PrintFace getInst(){
        if(mInst==null){
            synchronized (PrintFace.class){
                if(mInst==null){
                    mInst = new PrintFace();
                }
            }
        }
        return mInst;

    }

    public static void reset(){
        LogUtil.d(TAG,"reset");
        if(mInst!=null){
            mInst = null;
        }
    }

    /**
     * 获取大标签打印机
     * @return
     */
    private NetPrint getBigLabelPrinter(){
        int bigPrinter = PrintSetting.getBigPrinter(CSApplication.getInstance().getApplicationContext());
        if(bigPrinter==PrintSetting.FUJITSU){
            if(mBigPrinter!=null && mBigPrinter instanceof FujitsuPrinter){
                LogUtil.d(TAG,"大标签使用新版打印机");
                return mBigPrinter;
            }else{
                mBigPrinter = new FujitsuPrinter(BIGLABELIP,SMALLLABELIP,PORT);
                LogUtil.d(TAG,"大标签使用新版打印机");
                return mBigPrinter;
            }

        }else{
            if(mBigPrinter!=null && mBigPrinter instanceof WinposPrinter){
                LogUtil.d(TAG,"大标签使用旧版打印机");
                return mBigPrinter;
            }else{
                mBigPrinter = new WinposPrinter(BIGLABELIP,SMALLLABELIP,PORT);
                LogUtil.d(TAG,"大标签使用旧版打印机");
                return mBigPrinter;
            }
        }
    }

    /**
     * 获取小标签打印机
     * @return
     */
    private NetPrint getSmallLabelPrinter(){
        int smallPrinter = PrintSetting.getSmallPrinter(CSApplication.getInstance().getApplicationContext());
        if(smallPrinter==PrintSetting.FUJITSU){
            if(mSmallPrinter!=null && mSmallPrinter instanceof FujitsuPrinter){
                LogUtil.d(TAG,"小标签使用新版打印机");
                return mSmallPrinter;
            }else{
                mSmallPrinter = new FujitsuPrinter(BIGLABELIP,SMALLLABELIP,PORT);
                LogUtil.d(TAG,"小标签使用新版打印机");
                return mSmallPrinter;
            }

        }else{
            if(mSmallPrinter!=null && mSmallPrinter instanceof WinposPrinter){
                LogUtil.d(TAG,"小标签使用旧版打印机");
                return mSmallPrinter;
            }else{
                mSmallPrinter = new WinposPrinter(BIGLABELIP,SMALLLABELIP,PORT);
                LogUtil.d(TAG,"小标签使用旧版打印机");
                return mSmallPrinter;
            }
        }
    }


    /**
     * 同时打印一个订单的大小标签
     */
    public void startPrintWholeOrderTask(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getBigLabelPrinter().printBigLabel(order);
            }
        });
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getSmallLabelPrinter().printSmallLabel(order);
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
                getBigLabelPrinter().printSummaryInfo(batchOrders);

                //打印大标签
                getBigLabelPrinter().printBigLabels(batchOrders);
            }
        });

        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //打印小标签
                getSmallLabelPrinter().printSmallLabels(batchOrders);
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
                LogUtil.d(TAG,"startPrintOnlyBoxTask");
                getBigLabelPrinter().printBigLabel(order);
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
                getSmallLabelPrinter().printSmallLabel(order);
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
                getBigLabelPrinter().printMaterialBigLabel(materialItem);
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
                getSmallLabelPrinter().printTimeControlPaster(materialItem);
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
                getSmallLabelPrinter().printBlankPaster();
            }
        });
    }

    //检查打印机状态
    public void checkPrinterStatus() {
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getBigLabelPrinter().checkPrinterStatus(BIGLABELIP,PORT);
            }
        });
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getSmallLabelPrinter().checkPrinterStatus(SMALLLABELIP,PORT);
            }
        });
    }

    //打印测试
    public void printTest(final OrderBean order){
        mPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
//                getBigLabelPrinter().printBigLabel(order);
                NetPrint printter = getBigLabelPrinter();
                if(printter instanceof FujitsuPrinter){
                    ((FujitsuPrinter) printter).printQRcode();
                }
            }
        });
    }

}
