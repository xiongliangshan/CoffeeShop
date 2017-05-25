package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.delivery.model.DeliveringModel;
import com.lyancafe.coffeeshop.delivery.model.DeliveringModelImpl;
import com.lyancafe.coffeeshop.delivery.view.DeliveringView;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.bean.UserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/16
*/

public class DeliveringPresenterImpl implements DeliveringPresenter{


    private Context mContext;
    private DeliveringModel mDeliveringModel;
    private DeliveringView mDeliveringView;


    public DeliveringPresenterImpl(Context mContext, DeliveringView mDeliveringView) {
        this.mContext = mContext;
        this.mDeliveringView = mDeliveringView;
        mDeliveringModel = new DeliveringModelImpl();
    }

    @Override
    public void loadDeliveringOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mDeliveringModel.loadDeliveringOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> deliveringList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(2,deliveringList.size()));
                    mDeliveringView.bindDataToView(deliveringList);
                }else {
                    mDeliveringView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mDeliveringView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}