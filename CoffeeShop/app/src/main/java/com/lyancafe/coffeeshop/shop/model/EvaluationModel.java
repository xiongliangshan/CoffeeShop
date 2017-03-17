package com.lyancafe.coffeeshop.shop.model;

/**
* Created by Administrator on 2017/03/17
*/

public interface EvaluationModel{

    //加载评价列表数据
    void loadEvaluations(int lastOrderId, int type, EvaluationModelImpl.OnHandleEvaluationListener listener);


    //加载各个类型评价数量
    void loadEvaluationAmount(EvaluationModelImpl.OnHandleEvaluationListener listener);

}