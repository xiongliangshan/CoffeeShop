package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.OrderBean;

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
}
