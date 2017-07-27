package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;
import java.util.Map;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceView extends BaseView<OrderBean>{

    //从列表中删除某个item
    void removeItemFromList(int id);

    //显示开始生产对话框
    void showStartProduceConfirmDialog(final OrderBean orderBean);

    //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();


    //显示奶盖茶数量提示
    void showNaiGaiAmount(Map<String,Integer> map);
}