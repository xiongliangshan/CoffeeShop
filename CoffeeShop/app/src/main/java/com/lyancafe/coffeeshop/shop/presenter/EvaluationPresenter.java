package com.lyancafe.coffeeshop.shop.presenter;

import com.lyancafe.coffeeshop.shop.model.EvaluationModelImpl;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface EvaluationPresenter {

    //加载评价列表数据
    void loadEvaluations(int lastOrderId, int type);


  /*  //加载各个类型评价数量
    void loadEvaluationAmount();*/
}
