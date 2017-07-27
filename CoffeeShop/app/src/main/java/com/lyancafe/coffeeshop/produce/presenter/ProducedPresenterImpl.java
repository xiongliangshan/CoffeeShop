package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.produce.model.ProducedModel;
import com.lyancafe.coffeeshop.produce.model.ProducedModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducedView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducedPresenterImpl implements ProducedPresenter{

    private Context mContext;
    private ProducedView mProducedView;
    private ProducedModel mProduceModel;


    public ProducedPresenterImpl(Context mContext, ProducedView producedView) {
        this.mContext = mContext;
        this.mProducedView = producedView;
        mProduceModel = new ProducedModelImpl();
    }


    @Override
    public void loadToFetchOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProduceModel.loadToFetchOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> toFetchList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(2,toFetchList.size()));
                    mProducedView.bindDataToView(toFetchList);
                    OrderUtils.with().insertOrderList(toFetchList);
                }else {
                    mProducedView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mProducedView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}