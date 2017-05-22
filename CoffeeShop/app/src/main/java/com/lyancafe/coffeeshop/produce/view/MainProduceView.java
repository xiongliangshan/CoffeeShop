package com.lyancafe.coffeeshop.produce.view;

import java.util.Map;

/**
* Created by Administrator on 2017/03/15
*/

public interface MainProduceView{

    //显示奶盖茶数量提示
    void showNaiGaiAmount(Map<String,Integer> map);

    /**
     * 订单状态改变刷新列表
     * @param orderId
     * @param status 目标状态
     */
    void refreshListForStatus(long orderId,int status);
}