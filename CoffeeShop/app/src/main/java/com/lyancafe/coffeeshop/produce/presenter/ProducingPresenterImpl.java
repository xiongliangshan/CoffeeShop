package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.produce.model.ProducingModel;
import com.lyancafe.coffeeshop.produce.model.ProducingModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingPresenterImpl implements ProducingPresenter{

    private ProducingView mProducingView;
    private ProducingModel mProducingModel;
    private Context mContext;

    public ProducingPresenterImpl(ProducingView mProducingView, Context mContext) {
        this.mProducingView = mProducingView;
        this.mContext = mContext;
        mProducingModel = new ProducingModelImpl();
    }

    @Override
    public void loadProducingOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.loadProducingOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> producingList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(1,producingList.size()));
                    mProducingView.bindDataToListView(producingList);
                }else{
                    mProducingView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mProducingView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void doFinishProduced(long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.dodoFinishProduced(user.getShopId(), orderId, user.getToken(), new Observer<BaseEntity<JsonObject>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mProducingView.showLoading();
            }

            @Override
            public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                if(jsonObjectBaseEntity.getStatus()==0){
                    mProducingView.showToast(mContext.getString(R.string.do_success));
                    int id  = jsonObjectBaseEntity.getData().get("id").getAsInt();
                    mProducingView.removeItemFromList(id);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));
                }else{
                    mProducingView.showToast(jsonObjectBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mProducingView.dismissLoading();
                mProducingView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                mProducingView.dismissLoading();
            }
        });
    }
}