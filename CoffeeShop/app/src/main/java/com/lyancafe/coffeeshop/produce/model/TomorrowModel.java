package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/7/28.
 */

public interface TomorrowModel {

    //加载明日订单列表数据
    void loadTomorrowOrders(int shopId, String token, Observer<BaseEntity<List<OrderBean>>> observer);
}
