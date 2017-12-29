package com.lyancafe.coffeeshop.shop.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface ExceptionalModel {


    //加载异常订单列表
    void loadExceptionalOrdes(int shopId, CustomObserver<List<ExceptionalOrder>> observer);

    //重新指派配送团队
    void doRePush(int shopId,long orderId,int deliverTeam,CustomObserver<JsonObject> observer);

}
