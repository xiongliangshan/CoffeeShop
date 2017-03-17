package com.lyancafe.coffeeshop.delivery.presenter;


import android.app.Activity;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.common.HttpHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/16
*/

public class MainDeliverPresenterImpl implements MainDeliverPresenter{

    @Override
    public void reqRecallOrder(final Activity activity, long orderId) {
        HttpHelper.getInstance().reqRecallOrder(orderId, new DialogCallback<XlsResponse>(activity) {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                if (xlsResponse.status == 0) {
                    ToastUtil.showToast(activity, R.string.do_success);
                    int id = xlsResponse.data.getIntValue("id");
                    EventBus.getDefault().post(new RecallOrderEvent(10, id));
                } else {
                    ToastUtil.showToast(activity, xlsResponse.message);
                }
            }
        });
    }

}