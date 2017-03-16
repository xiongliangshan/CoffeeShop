package com.lyancafe.coffeeshop.delivery.presenter;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface ToFetchPresenter {

    //加载待取货列表数据
    void loadToFetchOrderList();

    //处理待取货列表接口返回结果
    void handleProudcedResponse(XlsResponse xlsResponse, Call call, Response response);
}
