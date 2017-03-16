package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.delivery.model.DeliveringModel;
import com.lyancafe.coffeeshop.delivery.model.DeliveringModelImpl;
import com.lyancafe.coffeeshop.delivery.view.DeliveringView;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/16
*/

public class DeliveringPresenterImpl implements DeliveringPresenter,DeliveringModelImpl.OnHandleDeliveringListener{


    private Context mContext;
    private DeliveringModel mDeliveringModel;
    private DeliveringView mDeliveringView;


    public DeliveringPresenterImpl(Context mContext, DeliveringView mDeliveringView) {
        this.mContext = mContext;
        this.mDeliveringView = mDeliveringView;
        mDeliveringModel = new DeliveringModelImpl();
    }

    @Override
    public void loadDeliveringOrderList() {
        mDeliveringModel.loadDeliveringOrderList(this);
    }

    @Override
    public void handleDeliveringResponse(XlsResponse xlsResponse, Call call, Response response) {
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(mContext, xlsResponse);
        EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(2,orderBeans.size()));
        mDeliveringView.addOrdersToList(orderBeans);
    }

    @Override
    public void onDeliveringSuccess(XlsResponse xlsResponse, Call call, Response response) {
        handleDeliveringResponse(xlsResponse,call,response);
    }

    @Override
    public void onDeliveringFailure(Call call, Response response, Exception e) {

    }
}