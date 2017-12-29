package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public interface TomorrowModel {

    //加载明日订单列表数据
    void loadTomorrowOrders(int shopId, CustomObserver<List<OrderBean>> observer);
}
