package com.lyancafe.coffeeshop.produce.presenter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface ProducingPresenter {

    //加载生产中列表
    void loadProducingOrders();

    //点击生产完成发送请求
    void doFinishProduced(long orderId);

    //生产动线点击完成
    void doFinishProducedFulfill();

    //点击批量完成生产
    void doCompleteBatchProduce(List<Long> orderIds);

}
