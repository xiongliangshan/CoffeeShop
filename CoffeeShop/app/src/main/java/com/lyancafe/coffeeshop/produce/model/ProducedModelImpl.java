package com.lyancafe.coffeeshop.produce.model;


import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducedModelImpl implements ProducedModel{


    @Override
    public void loadToFetchOrders(int shopId, CustomObserver<List<OrderBean>> observer) {
        RetrofitHttp.getRetrofit().loadToFetchOrders(shopId)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }
}