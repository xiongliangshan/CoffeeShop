package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingView{

    //数据设置到列表显示
    void addOrdersToList(List<OrderBean> orders);


    //从列表中删除某个item
    void removeItemFromList(int id);

    //显示生产完成确认对话框
    void showFinishProduceConfirmDialog(final OrderBean orderBean);
}