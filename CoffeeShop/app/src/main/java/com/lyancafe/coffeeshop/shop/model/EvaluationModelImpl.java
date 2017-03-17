package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class EvaluationModelImpl implements EvaluationModel{


    @Override
    public void loadEvaluations(int lastOrderId, int type, final OnHandleEvaluationListener listener) {
        HttpHelper.getInstance().reqEvaluationListData(lastOrderId, type, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadEvaluationListSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onLoadEvaluationListFailed(call,response,e);
            }
        });
    }

    @Override
    public void loadEvaluationAmount(final OnHandleEvaluationListener listener) {
        HttpHelper.getInstance().reqCommentCount(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoadTimeEffectAmountSuccess(xlsResponse,call,response);
            }
        });
    }

    public interface OnHandleEvaluationListener{
        void onLoadEvaluationListSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoadEvaluationListFailed(Call call, Response response, Exception e);
        void onLoadTimeEffectAmountSuccess(XlsResponse xlsResponse, Call call, Response response);
    }

}