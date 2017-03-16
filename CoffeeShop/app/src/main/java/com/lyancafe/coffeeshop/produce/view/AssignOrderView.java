package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.produce.model.DeliverBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface AssignOrderView{

    //小哥数据显示到列表
    void addDeliversToList(List<DeliverBean> courierBeanList);

}