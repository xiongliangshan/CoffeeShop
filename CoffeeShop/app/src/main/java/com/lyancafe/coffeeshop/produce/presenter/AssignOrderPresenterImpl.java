package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;

import com.lyancafe.coffeeshop.produce.model.DeliverBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class AssignOrderPresenterImpl implements AssignOrderPresenter{

    private AssignOrderView mAssignOrderView;
    private Context mContext;

    public AssignOrderPresenterImpl(AssignOrderView mAssignOrderView, Context mContext) {
        this.mAssignOrderView = mAssignOrderView;
        this.mContext = mContext;
    }

    @Override
    public void loadDelivers() {
        HttpHelper.getInstance().reqDeliverList(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleDeliverListResponse(xlsResponse, call, response);
            }
        });
    }

    @Override
    public void assignOrder(long orderId, long deliverId) {
        HttpHelper.getInstance().reqAssignOrder(orderId, deliverId, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleAssignOrderResponse(xlsResponse,call,response);
            }
        });
    }

    @Override
    public void handleDeliverListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<DeliverBean> courierBeanList = DeliverBean.parseJsonToDelivers(xlsResponse);
            mAssignOrderView.addDeliversToList(courierBeanList);
        }else{
            ToastUtil.show(mContext,xlsResponse.message);
        }
    }

    @Override
    public void handleAssignOrderResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            ToastUtil.show(mContext,"指派成功");
            if(mContext instanceof Activity){
                ((Activity) mContext).finish();
            }
        }else{
            ToastUtil.show(mContext,xlsResponse.message);
        }
    }
}