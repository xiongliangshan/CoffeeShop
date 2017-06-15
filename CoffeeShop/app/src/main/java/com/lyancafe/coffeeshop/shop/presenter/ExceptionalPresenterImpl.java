package com.lyancafe.coffeeshop.shop.presenter;

import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.shop.model.ExceptionalModel;
import com.lyancafe.coffeeshop.shop.model.ExceptionalModelImpl;
import com.lyancafe.coffeeshop.shop.view.ExceptionalView;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ExceptionalPresenterImpl implements ExceptionalPresenter {

    private ExceptionalModel mExceptionalModel;
    private ExceptionalView mExceptionalView;

    public ExceptionalPresenterImpl( ExceptionalView mExceptionalView) {
        this.mExceptionalView = mExceptionalView;
        mExceptionalModel = new ExceptionalModelImpl();
    }

    @Override
    public void loadExceptionalOrders() {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        mExceptionalModel.loadExceptionalOrdes(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<ExceptionalOrder>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<ExceptionalOrder>> listBaseEntity) {
                LogUtil.d(LogUtil.TAG_SHOP,"loadExceptionalOrders :"+listBaseEntity.toString());
                if(listBaseEntity.getStatus()==0){
                    List<ExceptionalOrder> list = listBaseEntity.getData();
                    mExceptionalView.bindDataToView(list);
                }else{
                    mExceptionalView.showToast(listBaseEntity.getMessage());
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mExceptionalView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void doRePush(final long orderId, int deliverTeam) {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        mExceptionalModel.doRePush(user.getShopId(), orderId, deliverTeam, user.getToken(), new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mExceptionalView.showLoading();
            }

            @Override
            public void onNext(@NonNull BaseEntity baseEntity) {
                if(baseEntity.getStatus()==0){
                    mExceptionalView.showToast("操作成功");
                    mExceptionalView.removeItemFromList(orderId);
                }else {
                    mExceptionalView.showToast(baseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mExceptionalView.dismissLoading();
                mExceptionalView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                mExceptionalView.dismissLoading();
            }
        });
    }
}
