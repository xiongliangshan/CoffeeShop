package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.shop.model.FinishedModel;
import com.lyancafe.coffeeshop.shop.model.FinishedModelImpl;
import com.lyancafe.coffeeshop.shop.view.FinishedView;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedPresenterImpl implements FinishedPresenter ,FinishedModelImpl.OnHandleResponseListener{

    private Context mContext;
    private FinishedModel mFinishedModel;
    private FinishedView mFinishedView;

    public FinishedPresenterImpl(Context mContext, FinishedView mFinishedView) {
        this.mContext = mContext;
        this.mFinishedView = mFinishedView;
        mFinishedModel = new FinishedModelImpl();
    }

    @Override
    public void loadFinishedOrders(long lastOrderId) {
        mFinishedModel.loadFinishedOrders(lastOrderId,this);
    }

    @Override
    public void loadOrderAmounts() {
        mFinishedModel.loadOrderAmounts(this);
    }

    /*@Override
    public void loadEffectPercent() {
        mFinishedModel.loadEffectPercent(this);
    }*/


    @Override
    public void onLoadListSuccess(XlsResponse xlsResponse, Call call, Response response) {
        mFinishedView.stopLoadingProgress();
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(mContext, xlsResponse);
        if ("yes".equalsIgnoreCase(isLoadMore)) {
            mFinishedView.appendListData(orderBeans);
            Log.d("xls", "addData orderBeans.size = " + orderBeans.size());
        } else {
            mFinishedView.bindDataToListView(orderBeans);
            Log.d("xls", "orderBeans.size = " + orderBeans.size());
        }
        mFinishedView.saveLastOrderId();
    }

    @Override
    public void onLoadListFailed(Call call, Response response, Exception e) {
        mFinishedView.stopLoadingProgress();
    }

    @Override
    public void onLoadAmountSuccess(XlsResponse xlsResponse, Call call, Response response) {
        if (xlsResponse.status == 0) {
            int ordersAmount = xlsResponse.data.getIntValue("totalOrdersAmount");
            int cupsAmount = xlsResponse.data.getIntValue("totalCupsAmount");
            mFinishedView.bindAmountDataToView(ordersAmount,cupsAmount);
        }
    }

   /* @Override
    public void onLoadTimeEffectSuccess(XlsResponse xlsResponse, Call call, Response response) {
        if (xlsResponse.status == 0) {
            double goodScale = xlsResponse.data.getDouble("goodScale");
            double passedScale = xlsResponse.data.getDouble("passedScale");
            double fallingScale = xlsResponse.data.getDouble("fallingScale");
            mFinishedView.bindTimeEffectDataToView(goodScale,passedScale,fallingScale);
            Log.d("finished", "goodScale = " + goodScale + ", passedScale = " + passedScale + ",fallingScale = " + fallingScale);
        }
    }*/
}