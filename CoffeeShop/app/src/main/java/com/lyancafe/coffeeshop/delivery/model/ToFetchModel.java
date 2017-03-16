package com.lyancafe.coffeeshop.delivery.model;

import com.lyancafe.coffeeshop.produce.model.ToProduceModelImpl;

/**
* Created by Administrator on 2017/03/15
*/

public interface ToFetchModel{


    //加载列表数据
    void loadToProduceOrderList(ToFetchModelImpl.OnHandleToFetchListener listener);

}