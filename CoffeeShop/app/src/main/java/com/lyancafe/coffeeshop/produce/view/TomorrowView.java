package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public interface TomorrowView<T> extends BaseView {

    //绑定数据到列表视图
    void bindDataToView(List<T> list);
}
