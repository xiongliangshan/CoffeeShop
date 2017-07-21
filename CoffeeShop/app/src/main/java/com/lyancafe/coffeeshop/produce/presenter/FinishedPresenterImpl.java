package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.produce.model.FinishedModel;
import com.lyancafe.coffeeshop.produce.model.FinishedModelImpl;
import com.lyancafe.coffeeshop.produce.view.FinishedView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedPresenterImpl implements FinishedPresenter {

    private Context mContext;
    private FinishedModel mFinishedModel;
    private FinishedView mFinishedView;

    public FinishedPresenterImpl(Context mContext, FinishedView mFinishedView) {
        this.mContext = mContext;
        this.mFinishedView = mFinishedView;
        mFinishedModel = new FinishedModelImpl();
    }

    @Override
    public void loadFinishedOrders(long lastOrderId, final boolean isLoadMore) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mFinishedModel.loadFinishedOrders(user.getShopId(), lastOrderId, user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> finishedList = listBaseEntity.getData();
                    if(isLoadMore){
                        mFinishedView.appendListData(finishedList);
                    }else{
                        mFinishedView.bindDataToView(finishedList);
                    }
                    mFinishedView.saveLastOrderId();
                    OrderUtils.with().insertOrderList(finishedList);
                }else{
                    mFinishedView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if(isLoadMore){
                    mFinishedView.stopLoadingProgress();
                }
                mFinishedView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                if(isLoadMore){
                    mFinishedView.stopLoadingProgress();
                }
            }
        });
    }




    @Override
    public void loadOrderAmount() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mFinishedModel.loadOrderAmount(user.getShopId(), user.getToken(), new Observer<BaseEntity<JsonObject>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                if(jsonObjectBaseEntity.getStatus()==0){
                    int ordersAmount = jsonObjectBaseEntity.getData().get("totalOrdersAmount").getAsInt();
                    int cupsAmount = jsonObjectBaseEntity.getData().get("totalCupsAmount").getAsInt();
                    mFinishedView.bindAmountDataToView(ordersAmount,cupsAmount);
                }else {
                    mFinishedView.showToast(jsonObjectBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mFinishedView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}