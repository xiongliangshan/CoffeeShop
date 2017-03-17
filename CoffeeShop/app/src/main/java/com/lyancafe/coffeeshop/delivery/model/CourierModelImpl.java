package com.lyancafe.coffeeshop.delivery.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/16
*/

public class CourierModelImpl implements CourierModel{

    @Override
    public void loadCouriers(final OnHandleCourierListener listener) {
        HttpHelper.getInstance().reqCourierList(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadCouriersSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onLoadCouriersFailure(call,response,e);
            }
        });
    }

    public interface OnHandleCourierListener{
        void onLoadCouriersSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoadCouriersFailure(Call call, Response response, Exception e);
    }
}