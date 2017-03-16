package com.lyancafe.coffeeshop.delivery.presenter;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface CourierPresenter {

    //加载小哥列表数据
    void loadCouriersList();

    //处理小哥列表接口返回结果
    void handleCourierListResponse(XlsResponse xlsResponse, Call call, Response response);

}