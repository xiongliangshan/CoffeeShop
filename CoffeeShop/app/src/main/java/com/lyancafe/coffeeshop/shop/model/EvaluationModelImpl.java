package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public class EvaluationModelImpl implements EvaluationModel{

    @Override
    public void loadEvaluations(int shopId, long orderId, int feedbackType, String token, CustomObserver<List<EvaluationBean>> observer) {
        RetrofitHttp.getRetrofit().loadEvaluations(shopId,orderId,feedbackType,token)
                .compose(RxHelper.<BaseEntity<List<EvaluationBean>>>io_main())
                .subscribe(observer);
    }
}