package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.delivery.model.ToFetchModel;
import com.lyancafe.coffeeshop.delivery.model.ToFetchModelImpl;
import com.lyancafe.coffeeshop.delivery.view.ToFetchView;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.bean.UserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/15
*/

public class ToFetchPresenterImpl implements ToFetchPresenter{

    private Context mContext;
    private ToFetchView mToFetchView;
    private ToFetchModel mToFetchModel;


    public ToFetchPresenterImpl(Context mContext, ToFetchView mToFetchView) {
        this.mContext = mContext;
        this.mToFetchView = mToFetchView;
        mToFetchModel = new ToFetchModelImpl();
    }


    @Override
    public void loadToFetchOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToFetchModel.loadToFetchOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> toFetchList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(1,toFetchList.size()));
                    mToFetchView.bindDataToListView(toFetchList);
                }else {
                    mToFetchView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mToFetchView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}