package com.lyancafe.coffeeshop.shop.model;

/**
* Created by Administrator on 2017/03/17
*/

public interface TimeEffectModel{


    //加载时效列表
    void loadTimeEffectList(int lastOrderId, int type, TimeEffectModelImpl.OnHandleTimeEffectListener listener);

    //加载各时效类别数量
    void loadTimeEffectAmount(TimeEffectModelImpl.OnHandleTimeEffectListener listener);

}