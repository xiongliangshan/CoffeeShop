package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SalesStatusOneDay;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public interface StatementModel {


    /**
     * 加载所有缓存订单
     * @return
     */
    List<OrderBean> loadAllCacheOrders();


    /**
     * 加载已配送完成订单
     * @return
     */
    List<OrderBean> loadFinishedOrders();

    /**
     * 加载门店日销售单子
     */
    void loadDailySales(int shopId, long time, CustomObserver<SalesStatusOneDay> observer);
}
