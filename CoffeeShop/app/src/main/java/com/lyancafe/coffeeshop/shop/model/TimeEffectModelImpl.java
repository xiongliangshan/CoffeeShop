package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class TimeEffectModelImpl implements TimeEffectModel{


    @Override
    public void loadTimeEffectList(int lastOrderId, int type, final OnHandleTimeEffectListener listener) {
        HttpHelper.getInstance().reqTimeEffectList(lastOrderId, type, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadListSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onLoadListFailed(call,response,e);
            }
        });
    }

    @Override
    public void loadTimeEffectAmount(final OnHandleTimeEffectListener listener) {
        HttpHelper.getInstance().reqTimeEffectTypeCount(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadTypeAmount(xlsResponse,call,response);
            }
        });
    }

    public interface OnHandleTimeEffectListener {
        void onLoadListSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoadListFailed(Call call, Response response, Exception e);
        void onLoadTypeAmount(XlsResponse xlsResponse, Call call, Response response);
    }
}