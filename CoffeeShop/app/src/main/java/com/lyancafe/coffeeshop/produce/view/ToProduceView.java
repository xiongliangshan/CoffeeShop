package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.produce.ui.ListMode;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceView<T> extends BaseView{

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

    //从列表中删除某个item
    void removeItemFromList(int id);

    //从列表中删除一个N个item
    void removeItemsFromList(List<Long> ids);

    //显示开始生产对话框
    void showStartProduceConfirmDialog(final OrderBean orderBean);

   /* //显示加载进度对话框
    void showLoading();

    //关闭进度对话框
    void dismissLoading();
*/

   /* //显示奶盖茶数量提示
    void showNaiGaiAmount(Map<String,Integer> map);*/

    //设定模式
    void setMode(ListMode mode);
}