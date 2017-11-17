package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.produce.model.FinishedModel;
import com.lyancafe.coffeeshop.produce.model.FinishedModelImpl;
import com.lyancafe.coffeeshop.produce.view.FinishedView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.annotations.NonNull;

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
        mFinishedModel.loadFinishedOrders(user.getShopId(), lastOrderId, user.getToken(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> finishedList = orderBeanList;
                if(isLoadMore){
                    mFinishedView.appendListData(finishedList);
                }else{
                    mFinishedView.bindDataToView(finishedList);
                }
                mFinishedView.saveLastOrderId();
                OrderUtils.with().insertOrderList(new CopyOnWriteArrayList<OrderBean>(finishedList));
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if(isLoadMore){
                    mFinishedView.stopLoadingProgress();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                if(isLoadMore){
                    mFinishedView.stopLoadingProgress();
                }
            }
        });
    }


    @Override
    public void loadOrderAmount() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mFinishedModel.loadOrderAmount(user.getShopId(), user.getToken(), new CustomObserver<JsonObject>(mContext) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                int ordersAmount = jsonObject.get("totalOrdersAmount").getAsInt();
                int cupsAmount = jsonObject.get("totalCupsAmount").getAsInt();
                mFinishedView.bindAmountDataToView(ordersAmount,cupsAmount);
            }
        });
    }
}