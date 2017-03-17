package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.produce.model.DeliverBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;

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
                if(xlsResponse.status==0){
                    List<DeliverBean> courierBeanList = DeliverBean.parseJsonToDelivers(xlsResponse);
                    mAssignOrderView.bindDataToListView(courierBeanList);
                }else{
                    mAssignOrderView.showToast(xlsResponse.message);
                }
            }
        });
    }

    @Override
    public void assignOrder(long orderId, long deliverId) {
        HttpHelper.getInstance().reqAssignOrder(orderId, deliverId, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                if(xlsResponse.status==0){
                    mAssignOrderView.showToast(mContext.getString(R.string.assign_success));
                    if(mContext instanceof Activity){
                        ((Activity) mContext).finish();
                    }
                }else{
                    mAssignOrderView.showToast(xlsResponse.message);
                }
            }
        });
    }

}