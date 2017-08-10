package com.lyancafe.coffeeshop.produce.model;


import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.BaseObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedModelImpl implements FinishedModel{


    @Override
    public void loadFinishedOrders(int shopId, long orderId, String token, BaseObserver<List<OrderBean>> observer) {
        RetrofitHttp.getRetrofit().loadFinishedOrders(shopId,orderId,token)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }

    @Override
    public void loadOrderAmount(int shopId, String token, BaseObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().loadOrderAmount(shopId,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }
}