package com.lyancafe.coffeeshop.delivery.model;

/**
* Created by Administrator on 2017/03/16
*/

public interface DeliveringModel{

    //加载列表数据
    void loadDeliveringOrderList(DeliveringModelImpl.OnHandleDeliveringListener listener);
}