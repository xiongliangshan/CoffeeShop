package com.lyancafe.coffeeshop.produce.view;

import android.content.Intent;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.produce.model.DeliverBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public interface AssignOrderView extends BaseView<DeliverBean>{

    //关闭当前Activity
    void finishAndStepToBack(long orderId);

    void showLoading();

    void dismissLoading();
}