package com.lyancafe.coffeeshop.produce.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingModelImpl implements ProducingModel{


    @Override
    public void loadProducingOrderList(final OnHandleProducingListener listener) {
        HttpHelper.getInstance().reqProducingData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onProducingSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onProducingFailure(call,response,e);
            }
        });
    }

    public interface OnHandleProducingListener{
        void onProducingSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onProducingFailure(Call call, Response response, Exception e);
    }
}