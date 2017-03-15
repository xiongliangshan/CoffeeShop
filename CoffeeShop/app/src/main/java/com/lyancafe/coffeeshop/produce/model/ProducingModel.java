package com.lyancafe.coffeeshop.produce.model;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducingModel{

    //加载生产中数据列表
    void loadProducingOrderList(ProducingModelImpl.OnHandleProducingListener listener);
}