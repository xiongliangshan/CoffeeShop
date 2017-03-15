package com.lyancafe.coffeeshop.produce.presenter;

import android.app.Activity;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface ToProducePresenter {

    //加载待生产列表
    void loadToProduceOrderList();

    //处理待生产列表返回结果
    void handleToProudceResponse(XlsResponse xlsResponse, Call call, Response response);

    //点击开始生产
    void reqStartProduceAndPrint(final Activity activity, final OrderBean order);

    //处理开始生产返回结果
    void handleStartProduceResponse(Activity activity,XlsResponse xlsResponse,Call call,Response response);
}
