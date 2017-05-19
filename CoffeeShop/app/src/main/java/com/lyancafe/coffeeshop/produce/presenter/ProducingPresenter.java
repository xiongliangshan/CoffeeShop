package com.lyancafe.coffeeshop.produce.presenter;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface ProducingPresenter {

    //加载生产中列表
    void loadProducingOrders();

    //点击生产完成发送请求
    void doFinishProduced(long orderId);

}
