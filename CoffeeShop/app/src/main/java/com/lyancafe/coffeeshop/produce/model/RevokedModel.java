package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public interface RevokedModel {

    //加载被撤销的订单
    List<OrderBean> loadRevokedOrders();
}
