package com.lyancafe.coffeeshop.delivery.model;

/**
* Created by Administrator on 2017/03/16
*/

public interface CourierModel{

    //加载小哥列表数据
    void loadCouriers(CourierModelImpl.OnHandleCourierListener listener);
}