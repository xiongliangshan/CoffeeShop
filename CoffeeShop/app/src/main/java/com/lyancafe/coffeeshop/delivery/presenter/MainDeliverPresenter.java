package com.lyancafe.coffeeshop.delivery.presenter;

import android.app.Activity;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface MainDeliverPresenter {
    //订单撤回请求
    void reqRecallOrder(final Activity activity, final long orderId);

    //处理订单撤回返回的结果
    void handleRecallOrderResponse(Activity activity, XlsResponse xlsResponse, Call call, Response response);
}
