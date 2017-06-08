package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface ExceptionalModel {


    //加载异常订单列表
    void loadExceptionalOrdes(int shopId, String token, Observer<BaseEntity<List<ExceptionalOrder>>> observer);

    //重新指派配送团队
    void doRePush(int shopId,long orderId,int deliverTeam,String token,Observer<BaseEntity> observer);

}
