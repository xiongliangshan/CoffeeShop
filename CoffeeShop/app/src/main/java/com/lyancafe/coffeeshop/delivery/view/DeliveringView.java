package com.lyancafe.coffeeshop.delivery.view;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/16
*/

public interface DeliveringView{

    //数据设置到列表显示
    void addOrdersToList(List<OrderBean> orders);
}