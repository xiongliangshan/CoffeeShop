package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.produce.view.MainProduceView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
* Created by Administrator on 2017/03/15
*/

public class MainProducePresenterImpl implements MainProducePresenter{

    private Context mContext;
    private MainProduceView mMainProduceView;

    public MainProducePresenterImpl(Context mContext, MainProduceView mMainProduceView) {
        this.mContext = mContext;
        this.mMainProduceView = mMainProduceView;
    }

    @Override
    public void doRecallOrder(long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        RetrofitHttp.getRetrofit().doRecallOrder(user.getShopId(),orderId,user.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseEntity<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                        if(jsonObjectBaseEntity.getStatus()==0){
                            ToastUtil.showToast(mContext.getApplicationContext(), R.string.do_success);
                            int id = jsonObjectBaseEntity.getData().get("id").getAsInt();
                            mMainProduceView.refreshListForStatus(id, OrderStatus.UNASSIGNED);
                        }else{
                            ToastUtil.showToast(mContext.getApplicationContext(), jsonObjectBaseEntity.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtil.showToast(mContext.getApplicationContext(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}