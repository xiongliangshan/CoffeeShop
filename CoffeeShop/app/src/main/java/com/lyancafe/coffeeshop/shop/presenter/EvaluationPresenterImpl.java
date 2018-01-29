package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.shop.model.EvaluationModel;
import com.lyancafe.coffeeshop.shop.model.EvaluationModelImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;

import java.util.List;

import io.reactivex.annotations.NonNull;

/**
* Created by Administrator on 2017/03/17
*/

public class EvaluationPresenterImpl implements EvaluationPresenter{

    private Context mContext;
    private EvaluationView mEvaluationView;
    private EvaluationModel mEvaluationModel;

    public EvaluationPresenterImpl(Context mContext, EvaluationView mEvaluationView) {
        this.mContext = mContext;
        this.mEvaluationView = mEvaluationView;
        mEvaluationModel = new EvaluationModelImpl();
    }


    @Override
    public void loadEvaluations(int lastId,final boolean isLoadMore) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mEvaluationModel.loadEvaluations(user.getShopId(), lastId, new CustomObserver<List<EvaluationBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<EvaluationBean> evaluationBeenList) {
                if(isLoadMore){
                    mEvaluationView.appendListData(evaluationBeenList);
                }else{
                    mEvaluationView.bindDataToView(evaluationBeenList);
                }
                mEvaluationView.saveLastFeedbackId();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if(isLoadMore){
                    mEvaluationView.stopLoadingProgress();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                if(isLoadMore){
                    mEvaluationView.stopLoadingProgress();
                }
            }
        });
    }
}