package com.lyancafe.coffeeshop.delivery.presenter;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface DeliveringPresenter {

    //加载待取货列表数据
    void loadDeliveringOrderList();

    //处理待取货列表接口返回结果
    void handleDeliveringResponse(XlsResponse xlsResponse, Call call, Response response);
}
