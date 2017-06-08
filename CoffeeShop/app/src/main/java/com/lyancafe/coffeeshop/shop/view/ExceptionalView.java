package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface ExceptionalView extends BaseView<ExceptionalOrder> {

    //显示无数据空视图
    void showEmpty(boolean isNeedToShow);

    void showLoading();

    void dismissLoading();

    //从列表中删除某个item
    void removeItemFromList(long orderId);
}
