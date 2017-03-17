package com.lyancafe.coffeeshop.shop.presenter;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface TimeEffectPresenter {

    //加载时效列表
    void loadTimeEffectList(int lastOrderId, int type);

    //加载各时效类别数量
    void loadTimeEffectAmount();
}
