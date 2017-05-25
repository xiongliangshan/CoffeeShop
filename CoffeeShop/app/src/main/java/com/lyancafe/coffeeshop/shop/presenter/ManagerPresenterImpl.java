package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.shop.model.ManagerModel;
import com.lyancafe.coffeeshop.shop.model.ManagerModelImpl;
import com.lyancafe.coffeeshop.shop.view.ManagerView;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/5/25.
 */

public class ManagerPresenterImpl implements ManagerPresenter {

    private ManagerModel mManagerModel;
    private ManagerView mManagerView;
    private Context mContext;

    public ManagerPresenterImpl(ManagerView mManagerView, Context mContext) {
        this.mManagerView = mManagerView;
        this.mContext = mContext;
        mManagerModel = new ManagerModelImpl();
    }

    @Override
    public void loadShopInfo() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mManagerModel.loadShopInfo(user.getShopId(), user.getToken(), new Observer<BaseEntity<ShopInfo>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<ShopInfo> shopInfoBaseEntity) {
                if(shopInfoBaseEntity.getStatus()==0){
                    ShopInfo shopInfo = shopInfoBaseEntity.getData();
                    mManagerView.bindShopInfoDataToView(shopInfo);
                }else{
                    mManagerView.showToast(shopInfoBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mManagerView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void modifyShopTelephone(String newPhoneNubmer) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mManagerModel.modifyShopTelephone(user.getShopId(), newPhoneNubmer, user.getToken(), new Observer<BaseEntity<JsonObject>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mManagerView.showLoading();
            }

            @Override
            public void onNext(@NonNull BaseEntity<JsonObject> jsonObjectBaseEntity) {
                if(jsonObjectBaseEntity.getStatus()==0){
                    JsonObject object = jsonObjectBaseEntity.getData();
                    String newPhone = object.get("shopTelephone").getAsString();
                    mManagerView.setTelephone(newPhone);
                }else{
                    mManagerView.showToast(jsonObjectBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mManagerView.dismissLoading();
                mManagerView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                mManagerView.dismissLoading();
                mManagerView.hideEdit();
            }
        });
    }
}
