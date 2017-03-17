package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.delivery.model.ToFetchModel;
import com.lyancafe.coffeeshop.delivery.model.ToFetchModelImpl;
import com.lyancafe.coffeeshop.delivery.view.ToFetchView;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class ToFetchPresenterImpl implements ToFetchPresenter,ToFetchModelImpl.OnHandleToFetchListener{

    private Context mContext;
    private ToFetchView mToFetchView;
    private ToFetchModel mToFetchModel;


    public ToFetchPresenterImpl(Context mContext, ToFetchView mToFetchView) {
        this.mContext = mContext;
        this.mToFetchView = mToFetchView;
        mToFetchModel = new ToFetchModelImpl();
    }

    @Override
    public void loadToFetchOrderList() {
        mToFetchModel.loadToProduceOrderList(this);
    }


    @Override
    public void onToFetchSuccess(XlsResponse xlsResponse, Call call, Response response) {
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(mContext, xlsResponse);
        EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(1,orderBeans.size()));
        mToFetchView.bindDataToListView(orderBeans);
    }

    @Override
    public void onToFetchFailure(Call call, Response response, Exception e) {

    }
}