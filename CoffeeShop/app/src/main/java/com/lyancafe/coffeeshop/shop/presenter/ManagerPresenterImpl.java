package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.shop.model.ManagerModel;
import com.lyancafe.coffeeshop.shop.model.ManagerModelImpl;
import com.lyancafe.coffeeshop.shop.view.ManagerView;

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
        mManagerModel.loadShopInfo(user.getShopId(), user.getToken(), new CustomObserver<ShopInfo>(mContext) {
            @Override
            protected void onHandleSuccess(ShopInfo shopInfo) {
                mManagerView.bindShopInfoDataToView(shopInfo);
            }
        });
    }


    @Override
    public void modifyShopTelephone(String newPhoneNubmer) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mManagerModel.modifyShopTelephone(user.getShopId(), newPhoneNubmer, user.getToken(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                JsonObject object = jsonObject;
                String newPhone = object.get("shopTelephone").getAsString();
                mManagerView.setTelephone(newPhone);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                mManagerView.hideEdit();
            }

        });
    }
}
