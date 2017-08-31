package com.lyancafe.coffeeshop.produce.model;


import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProduceModelImpl implements ToProduceModel{




    @Override
    public void loadToProduceOrders(int shopId, String token, CustomObserver<List<OrderBean>> observer) {
        RetrofitHttp.getRetrofit().loadToProduceOrders(shopId,token)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }

    @Override
    public void doStartProduce(int shopId, long orderId, String token, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doStartProduce(shopId,orderId,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);

    }

    @Override
    public void doStartBatchProduce(int shopId, List<Long> orderIds, String token, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doStartBatchProduce(shopId,orderIds,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }

    @Override
    public void doNoProduce(int shopId, long orderId, String token, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doNoProduce(shopId,orderId,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }
}