package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingView<T> extends BaseView{

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

    //从列表中删除某个item
    void removeItemFromList(int id);

    //显示生产完成确认对话框
    void showFinishProduceConfirmDialog(final OrderBean orderBean);

   /* //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();*/

    //从列表中删除一个N个item
    void removeItemsFromList(List<Long> ids);
}