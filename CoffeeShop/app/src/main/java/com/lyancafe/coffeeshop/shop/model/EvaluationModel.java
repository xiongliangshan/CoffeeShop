package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.EvaluationBean;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/17
*/

public interface EvaluationModel{

    //加载评价列表数据
    void loadEvaluations(int shopId, long orderId, int feedbackType, String token, Observer<BaseEntity<List<EvaluationBean>>> observer);

}