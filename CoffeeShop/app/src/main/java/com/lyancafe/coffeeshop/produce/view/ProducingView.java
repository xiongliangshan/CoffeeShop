package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingView extends BaseView<OrderBean>{

    //从列表中删除某个item
    void removeItemFromList(int id);

    //显示生产完成确认对话框
    void showFinishProduceConfirmDialog(final OrderBean orderBean);

    //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();
}