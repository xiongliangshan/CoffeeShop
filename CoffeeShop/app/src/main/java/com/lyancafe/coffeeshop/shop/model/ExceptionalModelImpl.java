package com.lyancafe.coffeeshop.shop.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ExceptionalModelImpl implements ExceptionalModel {

    @Override
    public void loadExceptionalOrdes(int shopId, String token, CustomObserver<List<ExceptionalOrder>> observer) {
        RetrofitHttp.getRetrofit().loadExceptionalOrders(shopId,token)
                .compose(RxHelper.<BaseEntity<List<ExceptionalOrder>>>io_main())
                .subscribe(observer);
    }

    @Override
    public void doRePush(int shopId, long orderId, int deliverTeam, String token, CustomObserver<JsonObject> observer) {
        RetrofitHttp.getRetrofit().doRePush(shopId,orderId,deliverTeam,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }
}
