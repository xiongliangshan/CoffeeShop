package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.login.ui.LoginActivity;
import com.lyancafe.coffeeshop.produce.model.ToProduceModel;
import com.lyancafe.coffeeshop.produce.model.ToProduceModelImpl;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProducePresenterImpl implements ToProducePresenter{

    private Context mContext;
    private ToProduceModel mToProduceModel;
    private ToProduceView mToProduceView;

    public ToProducePresenterImpl(Context mContext, ToProduceView mToProduceView) {
        this.mContext = mContext;
        this.mToProduceView = mToProduceView;
        mToProduceModel = new ToProduceModelImpl();
    }



    @Override
    public void loadToProduceOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.loadToProduceOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> toProduceList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(0, toProduceList.size()));
                    mToProduceView.bindDataToView(toProduceList);
                }else if(listBaseEntity.getStatus()==103){
                    mToProduceView.showToast(listBaseEntity.getMessage());
                    UserBean userBean = LoginHelper.getUser(mContext);
                    userBean.setToken("");
                    LoginHelper.saveUser(mContext, userBean);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    if(mContext!=null && mContext instanceof Activity){
                        ((Activity) mContext).finish();
                    }
                }else{
                    mToProduceView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mToProduceView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }



    @Override
    public void doStartProduce(long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.doStartProduce(user.getShopId(), orderId, user.getToken(), new Observer<BaseEntity<JsonObject>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mToProduceView.showLoading();
            }

            @Override
            public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                if(jsonObjectBaseEntity.getStatus()==0){
                    mToProduceView.showToast(mContext.getString(R.string.do_success));
                    int id  = jsonObjectBaseEntity.getData().get("id").getAsInt();
                    mToProduceView.removeItemFromList(id);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1));
                }else{
                    mToProduceView.showToast(jsonObjectBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mToProduceView.dismissLoading();
                mToProduceView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                mToProduceView.dismissLoading();
            }
        });
    }
}