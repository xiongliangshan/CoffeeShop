package com.lyancafe.coffeeshop.produce.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProduceModelImpl implements ToProduceModel{


    @Override
    public void loadToProduceOrderList(final OnHandleToProduceListener listener) {
        HttpHelper.getInstance().reqToProduceData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onToProSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onToProFailure(call,response,e);
            }
        });
    }








    public interface OnHandleToProduceListener{
        void onToProSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onToProFailure(Call call, Response response, Exception e);
    }
}