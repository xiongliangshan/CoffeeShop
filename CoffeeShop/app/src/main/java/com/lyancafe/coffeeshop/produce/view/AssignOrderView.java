package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.DeliverBean;

/**
* Created by Administrator on 2017/03/15
*/

public interface AssignOrderView extends BaseView<DeliverBean>{

    //关闭当前Activity
    void finishAndStepToBack(long orderId);

    void showLoading();

    void dismissLoading();
}