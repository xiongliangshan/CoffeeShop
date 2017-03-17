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

    //点击开始生产
    void reqStartProduceAndPrint(final Activity activity, final OrderBean order);

}
