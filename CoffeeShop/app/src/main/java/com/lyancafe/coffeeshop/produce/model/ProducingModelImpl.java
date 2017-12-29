package com.lyancafe.coffeeshop.produce.model;


import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingModelImpl implements ProducingModel{


    @Override
    public void loadProducingOrders(int shopId, CustomObserver<List<OrderBean>> observer) {
        RetrofitHttp.getRetrofit().loadProducingOrders(shopId)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }


    @Override
    public void dodoFinishProduced(int shopId, long orderId, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doFinishProduced(shopId,orderId)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }


    @Override
    public void doCompleteBatchProduce(int shopId, List<Long> orderIds, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doCompleteBatchProduce(shopId,orderIds)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }
}