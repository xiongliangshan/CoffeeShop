package com.lyancafe.coffeeshop.produce.model;


import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedModelImpl implements FinishedModel{


    @Override
    public void loadFinishedOrders(int shopId, long orderId, CustomObserver<List<OrderBean>> observer) {
        RetrofitHttp.getRetrofit().loadFinishedOrders(shopId,orderId)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }

    @Override
    public void loadOrderAmount(int shopId, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().loadOrderAmount(shopId)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }
}