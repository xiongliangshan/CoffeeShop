package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.bean.DeliverBean;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
                .compose(RxHelper.<BaseEntity<List<DeliverBean>>>io_main())
                .subscribe(new Observer<BaseEntity<List<DeliverBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mAssignOrderView.showLoading();
                    }

                    @Override
                    public void onNext(@NonNull BaseEntity<List<DeliverBean>> listBaseEntity) {
                        if(listBaseEntity.getStatus()==0){
                            List<DeliverBean> deliverList = listBaseEntity.getData();
                            mAssignOrderView.bindDataToView(deliverList);
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
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
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