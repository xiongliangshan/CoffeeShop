package com.lyancafe.coffeeshop.produce.presenter;

import android.app.Activity;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface ProducingPresenter {

    //加载生产中列表
    void loadProducingOrderList();

    //处理待生产列表返回结果
    void handleProudcingResponse(XlsResponse xlsResponse, Call call, Response response);

    //点击生产完成发送请求
    void reqFinishProduce(final Activity activity, final OrderBean order);

    //处理生产完成返回结果
    void handleFinishedProduceResponse(Activity activity,XlsResponse xlsResponse,Call call,Response response);
}
