package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceView extends BaseView<OrderBean>{

    //从列表中删除某个item
    void removeItemFromList(int id);

    //显示开始生产对话框
    void showStartProduceConfirmDialog(final OrderBean orderBean);
}