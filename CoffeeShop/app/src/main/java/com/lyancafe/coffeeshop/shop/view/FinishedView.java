package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface FinishedView extends BaseView<OrderBean>{


    //追加列表数据
    void appendListData(List<OrderBean> list);


    /**
     *  绑定订单总量数据到视图
     * @param ordersAmount  总单量
     * @param cupsAmount  总杯量
     */
    void bindAmountDataToView(int ordersAmount , int cupsAmount);


    /**
     * 绑定时效百分比数据到视图
     * @param goodScale 良好
     * @param passedScale 合格
     * @param fallingScale 不及格
     */
    void bindTimeEffectDataToView(double goodScale,double passedScale,double fallingScale);

    //保存lastOrderId
    void saveLastOrderId();

    //停止加载进度对话框
    void stopLoadingProgress();

}