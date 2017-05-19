package com.lyancafe.coffeeshop.delivery.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/16
*/

public interface CourierModel{

    //加载小哥列表数据
    void loadCouriers(int shopId, String token, Observer<BaseEntity<List<CourierBean>>> observer);
}