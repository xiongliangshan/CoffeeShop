package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.produce.ui.MainProduceFragment;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class MainProducePresenterImpl implements MainProducePresenter{



    @Override
    public void reqRecallOrder(final Activity activity,long orderId) {
        HttpHelper.getInstance().reqRecallOrder(orderId, new DialogCallback<XlsResponse>(activity) {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleRecallOrderResponse(activity,xlsResponse, call, response);
            }
        });
    }


    @Override
    public void handleRecallOrderResponse(Activity activity,XlsResponse xlsResponse, Call call, Response response) {
        if (xlsResponse.status == 0) {
            ToastUtil.showToast(activity, R.string.do_success);
            int id = xlsResponse.data.getIntValue("id");
            EventBus.getDefault().post(new RecallOrderEvent(MainProduceFragment.tabIndex, id));
        } else {
            ToastUtil.showToast(activity, xlsResponse.message);
        }
    }
}