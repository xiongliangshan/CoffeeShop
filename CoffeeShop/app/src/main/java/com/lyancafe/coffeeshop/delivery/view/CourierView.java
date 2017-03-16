package com.lyancafe.coffeeshop.delivery.view;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.delivery.model.CourierBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/16
*/

public interface CourierView{

    //数据设置到列表显示
    void addCouriersToList(List<CourierBean> couriers);
}