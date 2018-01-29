package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface FinishedView<T> extends BaseView{

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

    //追加列表数据
    void appendListData(List<T> list);


    /**
     *  绑定订单总量数据到视图
     * @param ordersAmount  总单量
     * @param cupsAmount  总杯量
     */
    void bindAmountDataToView(int ordersAmount , int cupsAmount);


    //保存lastOrderId
    void saveLastOrderId();

    //停止加载进度对话框
    void stopLoadingProgress();


    //显示无数据空视图
    void showEmpty(boolean isNeedToShow);

}