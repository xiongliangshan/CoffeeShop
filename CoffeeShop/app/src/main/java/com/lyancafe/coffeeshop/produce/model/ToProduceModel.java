package com.lyancafe.coffeeshop.produce.model;

/**
* Created by Administrator on 2017/03/14
*/

public interface ToProduceModel{

    //加载列表数据
    void loadToProduceOrderList(ToProduceModelImpl.OnHandleToProduceListener listener);

}