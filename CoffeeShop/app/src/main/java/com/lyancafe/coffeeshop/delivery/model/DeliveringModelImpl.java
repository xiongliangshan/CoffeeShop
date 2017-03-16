package com.lyancafe.coffeeshop.delivery.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/16
*/

public class DeliveringModelImpl implements DeliveringModel{


    @Override
    public void loadDeliveringOrderList(final OnHandleDeliveringListener listener) {
        HttpHelper.getInstance().reqDeliveryingData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onDeliveringSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onDeliveringFailure(call,response,e);
            }
        });
    }

    public interface OnHandleDeliveringListener{
        void onDeliveringSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onDeliveringFailure(Call call, Response response, Exception e);
    }

}