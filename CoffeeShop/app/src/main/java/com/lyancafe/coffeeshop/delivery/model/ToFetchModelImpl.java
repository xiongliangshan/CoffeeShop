package com.lyancafe.coffeeshop.delivery.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class ToFetchModelImpl implements ToFetchModel{


    @Override
    public void loadToProduceOrderList(final OnHandleToFetchListener listener) {
        HttpHelper.getInstance().reqProducedData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onToFetchSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onToFetchFailure(call,response,e);
            }
        });
    }

    public interface OnHandleToFetchListener{
        void onToFetchSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onToFetchFailure(Call call, Response response, Exception e);
    }
}