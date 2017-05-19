package com.lyancafe.coffeeshop.shop.model;


import android.text.AndroidCharacter;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class EvaluationModelImpl implements EvaluationModel{

    @Override
    public void loadEvaluations(int shopId, long orderId, int feedbackType, String token, Observer<BaseEntity<List<EvaluationBean>>> observer) {
        RetrofitHttp.getRetrofit().loadEvaluations(shopId,orderId,feedbackType,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}