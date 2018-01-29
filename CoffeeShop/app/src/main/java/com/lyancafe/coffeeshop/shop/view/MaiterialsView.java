package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface MaiterialsView<T> extends BaseView{

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

    void showContentLoading();

    void dismissContentLoading();
}