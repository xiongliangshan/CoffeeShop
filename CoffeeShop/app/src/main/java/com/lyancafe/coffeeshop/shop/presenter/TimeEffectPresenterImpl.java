package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.TimeEffectBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.shop.model.TimeEffectModel;
import com.lyancafe.coffeeshop.shop.model.TimeEffectModelImpl;
import com.lyancafe.coffeeshop.shop.view.TimeEffectView;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class TimeEffectPresenterImpl implements TimeEffectPresenter,TimeEffectModelImpl.OnHandleTimeEffectListener{

    private Context mContext;
    private TimeEffectModel mTimeEffectModel;
    private TimeEffectView mTimeEffectView;

    public TimeEffectPresenterImpl(Context mContext, TimeEffectView mTimeEffectView) {
        this.mContext = mContext;
        this.mTimeEffectView = mTimeEffectView;
        mTimeEffectModel = new TimeEffectModelImpl();
    }

    @Override
    public void loadTimeEffectList(int lastOrderId, int type) {
        mTimeEffectModel.loadTimeEffectList(lastOrderId,type,this);
    }

    @Override
    public void loadTimeEffectAmount() {
        mTimeEffectModel.loadTimeEffectAmount(this);
    }

    @Override
    public void onLoadListSuccess(XlsResponse xlsResponse, Call call, Response response) {
        mTimeEffectView.stopLoadingProgress();
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<TimeEffectBean> timeEffectBeanList = TimeEffectBean.parseJsonOrders(mContext,xlsResponse);
        Log.d("xls","list.size = "+timeEffectBeanList.size());
        if("yes".equalsIgnoreCase(isLoadMore)){
            mTimeEffectView.appendListData(timeEffectBeanList);
        }else{
            mTimeEffectView.bindDataToListView(timeEffectBeanList);
        }
        mTimeEffectView.saveLastOrderId();
    }

    @Override
    public void onLoadListFailed(Call call, Response response, Exception e) {
        mTimeEffectView.stopLoadingProgress();
    }

    @Override
    public void onLoadTypeAmount(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            int totalCount = xlsResponse.data.getIntValue("totalCount");
            int goodCount =xlsResponse.data.getIntValue("goodCount");
            int passedCount = xlsResponse.data.getIntValue("passedCount");
            int fallingCount = xlsResponse.data.getIntValue("fallingCount");
            mTimeEffectView.bindTimeEffctAmount(totalCount,goodCount,passedCount,fallingCount);
        }
    }
}