package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.DeliverBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

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
                .subscribe(new CustomObserver<List<DeliverBean>>(mContext) {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        mAssignOrderView.showLoading();
                    }

                    @Override
                    protected void onHandleSuccess(List<DeliverBean> deliverBeenList) {
                        List<DeliverBean> deliverList = deliverBeenList;
                        mAssignOrderView.bindDataToView(deliverList);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mAssignOrderView.dismissLoading();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        mAssignOrderView.dismissLoading();
                    }
                });

    }


    @Override
    public void doAssignOrder(long orderId, long courierId) {
        UserBean user  = LoginHelper.getUser(mContext.getApplicationContext());
        RetrofitHttp.getRetrofit().doAssignOrder(user.getShopId(),orderId,courierId,user.getToken())
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(new CustomObserver<JsonObject>(mContext,true) {
                    @Override
                    protected void onHandleSuccess(JsonObject jsonObject) {
                        mAssignOrderView.showToast(mContext.getString(R.string.assign_success));
                        long id = jsonObject.get("id").getAsLong();
                        mAssignOrderView.finishAndStepToBack(id);
                    }
                });


    }
}