package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.shop.model.EvaluationModel;
import com.lyancafe.coffeeshop.shop.model.EvaluationModelImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

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
    public void loadEvaluations(long lastOrderId, int type, final boolean isLoadMore) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mEvaluationModel.loadEvaluations(user.getShopId(), lastOrderId, type, user.getToken(), new Observer<BaseEntity<List<EvaluationBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull BaseEntity<List<EvaluationBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<EvaluationBean> evaluationsList = listBaseEntity.getData();
                    if(isLoadMore){
                        mEvaluationView.appendListData(evaluationsList);
                    }else{
                        mEvaluationView.bindDataToListView(evaluationsList);
                    }
                    mEvaluationView.saveLastOrderId();
                }else {
                    mEvaluationView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if(isLoadMore){
                    mEvaluationView.stopLoadingProgress();
                }
                mEvaluationView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                if(isLoadMore){
                    mEvaluationView.stopLoadingProgress();
                }

            }
        });
    }
}