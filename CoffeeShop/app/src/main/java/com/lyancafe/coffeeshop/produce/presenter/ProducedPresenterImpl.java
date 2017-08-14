package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.produce.model.ProducedModel;
import com.lyancafe.coffeeshop.produce.model.ProducedModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducedView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
        mProduceModel.loadToFetchOrders(user.getShopId(), user.getToken(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> toFetchList = orderBeanList;
                EventBus.getDefault().post(new UpdateTabCount(2,toFetchList.size()));
                mProducedView.bindDataToView(toFetchList);
                OrderUtils.with().insertOrderList(toFetchList);
            }
        });
    }
}