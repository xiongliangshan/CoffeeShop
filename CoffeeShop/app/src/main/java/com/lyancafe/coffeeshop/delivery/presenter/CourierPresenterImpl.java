package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.delivery.model.CourierBean;
import com.lyancafe.coffeeshop.delivery.model.CourierModel;
import com.lyancafe.coffeeshop.delivery.model.CourierModelImpl;
import com.lyancafe.coffeeshop.delivery.view.CourierView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/16
*/

public class CourierPresenterImpl implements CourierPresenter,CourierModelImpl.OnHandleCourierListener{

    private Context mContext;
    private CourierView mCourierView;
    private CourierModel mCourierModel;

    public CourierPresenterImpl(Context mContext, CourierView mCourierView) {
        this.mContext = mContext;
        this.mCourierView = mCourierView;
        mCourierModel = new CourierModelImpl();
    }

    @Override
    public void loadCouriersList() {
        mCourierModel.loadCouriers(this);
    }

    @Override
    public void handleCourierListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<CourierBean> couriers = CourierBean.parseJsonToCouriers(xlsResponse);
            mCourierView.addCouriersToList(couriers);
        }else{
            ToastUtil.show(mContext,xlsResponse.message);
        }
    }


    @Override
    public void onLoadCouriersSuccess(XlsResponse xlsResponse, Call call, Response response) {
        handleCourierListResponse(xlsResponse,call,response);
    }

    @Override
    public void onLoadCouriersFailure(Call call, Response response, Exception e) {

    }
}