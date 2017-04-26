package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.shop.model.EvaluationModel;
import com.lyancafe.coffeeshop.shop.model.EvaluationModelImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class EvaluationPresenterImpl implements EvaluationPresenter,EvaluationModelImpl.OnHandleEvaluationListener{

    private Context mContext;
    private EvaluationView mEvaluationView;
    private EvaluationModel mEvaluationModel;

    public EvaluationPresenterImpl(Context mContext, EvaluationView mEvaluationView) {
        this.mContext = mContext;
        this.mEvaluationView = mEvaluationView;
        mEvaluationModel = new EvaluationModelImpl();
    }

    @Override
    public void loadEvaluations(int lastOrderId, int type) {
        mEvaluationModel.loadEvaluations(lastOrderId,type,this);
    }

   /* @Override
    public void loadEvaluationAmount() {
        mEvaluationModel.loadEvaluationAmount(this);
    }*/

    @Override
    public void onLoadEvaluationListSuccess(XlsResponse xlsResponse, Call call, Response response) {
        mEvaluationView.stopLoadingProgress();
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<EvaluationBean> list = EvaluationBean.parseJsonOrders(mContext,xlsResponse);
        if("yes".equalsIgnoreCase(isLoadMore)){
            mEvaluationView.appendListData(list);
        }else{
            mEvaluationView.bindDataToListView(list);
        }
        mEvaluationView.saveLastOrderId();
    }

    @Override
    public void onLoadEvaluationListFailed(Call call, Response response, Exception e) {
        mEvaluationView.stopLoadingProgress();
    }

   /* @Override
    public void onLoadTimeEffectAmountSuccess(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            int positive = xlsResponse.data.getIntValue("positive");
            int negative = xlsResponse.data.getIntValue("negative");
            mEvaluationView.bindEvaluationAmount(positive,negative);

        }
    }*/
}