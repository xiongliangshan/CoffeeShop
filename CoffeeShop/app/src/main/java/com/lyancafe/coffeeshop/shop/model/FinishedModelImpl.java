package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class FinishedModelImpl implements FinishedModel{


    @Override
    public void loadFinishedOrders(long lastOrderId,final OnHandleResponseListener listener) {
        HttpHelper.getInstance().reqFinishedListData(lastOrderId, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadListSuccess(xlsResponse, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onLoadListFailed(call,response,e);
            }
        });
    }

    @Override
    public void loadOrderAmounts(final OnHandleResponseListener listener) {
        HttpHelper.getInstance().reqFinishedTotalAmountData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadAmountSuccess(xlsResponse, call, response);
            }

        });
    }

    @Override
    public void loadEffectPercent(final OnHandleResponseListener listener) {
        HttpHelper.getInstance().reqServiceEffectPersent(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadTimeEffectSuccess(xlsResponse, call, response);
            }
        });
    }

    public interface OnHandleResponseListener{
        void onLoadListSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoadListFailed(Call call, Response response, Exception e);
        void onLoadAmountSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoadTimeEffectSuccess(XlsResponse xlsResponse, Call call, Response response);
    }
}