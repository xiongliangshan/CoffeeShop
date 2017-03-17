package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface TimeEffectView extends BaseView<TimeEffectBean>{

    //追加列表数据
    void appendListData(List<TimeEffectBean> list);

    //保存lastOrderId
    void saveLastOrderId();

    //停止加载进度对话框
    void stopLoadingProgress();

    //绑定各类型时效数量
    void bindTimeEffctAmount(int totalCount, int goodCount, int passedCount, int fallingCount);

}