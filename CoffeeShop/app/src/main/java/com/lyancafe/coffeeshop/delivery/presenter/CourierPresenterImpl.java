package com.lyancafe.coffeeshop.delivery.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.bean.CourierBean;
import com.lyancafe.coffeeshop.delivery.model.CourierModel;
import com.lyancafe.coffeeshop.delivery.model.CourierModelImpl;
import com.lyancafe.coffeeshop.delivery.view.CourierView;
import com.lyancafe.coffeeshop.bean.UserBean;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/16
*/

public class CourierPresenterImpl implements CourierPresenter{

    private Context mContext;
    private CourierView mCourierView;
    private CourierModel mCourierModel;

    public CourierPresenterImpl(Context mContext, CourierView mCourierView) {
        this.mContext = mContext;
        this.mCourierView = mCourierView;
        mCourierModel = new CourierModelImpl();
    }


    @Override
    public void loadCouriers() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mCourierModel.loadCouriers(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<CourierBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<CourierBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<CourierBean> couriers = listBaseEntity.getData();
                    mCourierView.bindDataToListView(couriers);
                }else {
                    mCourierView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mCourierView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}