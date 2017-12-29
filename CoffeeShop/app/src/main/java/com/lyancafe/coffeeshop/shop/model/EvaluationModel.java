package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface EvaluationModel{

    //加载评价列表数据
    void loadEvaluations(int shopId, int id, CustomObserver<List<EvaluationBean>> observer);

}