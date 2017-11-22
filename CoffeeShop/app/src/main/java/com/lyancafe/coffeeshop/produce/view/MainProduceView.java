package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;

/**
* Created by Administrator on 2017/03/15
*/

public interface MainProduceView extends BaseView{


    /**
     * 订单状态改变刷新列表
     * @param orderId
     * @param status 目标状态
     */
    void refreshListForStatus(long orderId,int status);
}