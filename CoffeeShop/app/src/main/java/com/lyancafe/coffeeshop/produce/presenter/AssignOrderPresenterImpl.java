package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.produce.model.DeliverBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;
import com.lyancafe.coffeeshop.produce.ui.AssignOrderActivity;
import com.lyancafe.coffeeshop.produce.ui.MainProduceFragment;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    public void loadDeliversForAssign() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        RetrofitHttp.getRetrofit().loadDeliversForAssign(user.getShopId(),user.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseEntity<List<DeliverBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mAssignOrderView.showLoading();
                    }

                    @Override
                    public void onNext(@NonNull BaseEntity<List<DeliverBean>> listBaseEntity) {
                        if(listBaseEntity.getStatus()==0){
                            List<DeliverBean> deliverList = listBaseEntity.getData();
                            mAssignOrderView.bindDataToListView(deliverList);
                        }else{
                            mAssignOrderView.showToast(listBaseEntity.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mAssignOrderView.dismissLoading();
                        mAssignOrderView.showToast(e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        mAssignOrderView.dismissLoading();
                    }
                });
    }


    @Override
    public void doAssignOrder(long orderId, long courierId) {
        UserBean user  = LoginHelper.getUser(mContext.getApplicationContext());
        RetrofitHttp.getRetrofit().doAssignOrder(user.getShopId(),orderId,courierId,user.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseEntity<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                        if(jsonObjectBaseEntity.getStatus()==0){
                            mAssignOrderView.showToast(mContext.getString(R.string.assign_success));
                            long id = jsonObjectBaseEntity.getData().get("id").getAsLong();
                            mAssignOrderView.finishAndStepToBack(id);
                        }else{
                            mAssignOrderView.showToast(jsonObjectBaseEntity.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mAssignOrderView.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}