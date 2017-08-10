package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.shop.model.MaterialsModel;
import com.lyancafe.coffeeshop.shop.model.MaterialsModelImpl;
import com.lyancafe.coffeeshop.shop.view.MaiterialsView;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/17
*/

public class MaterialsPresenterImpl implements MaterialsPresenter{

    private Context mContext;
    private MaterialsModel mMaterialModel;
    private MaiterialsView mMaterialView;

    public MaterialsPresenterImpl(Context mContext, MaiterialsView mShopManagerView) {
        this.mContext = mContext;
        this.mMaterialView = mShopManagerView;
        mMaterialModel = new MaterialsModelImpl();
    }


    @Override
    public void loadMaterials() {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        mMaterialModel.loadMaterials(user.getShopId(), user.getToken(), new CustomObserver<List<Material>>(mContext) {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                mMaterialView.showLoading();
            }

            @Override
            protected void onHandleSuccess(List<Material> materialList) {
                List<Material> materials = materialList;
                mMaterialView.bindDataToView(materials);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                mMaterialView.dismissLoading();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                mMaterialView.dismissLoading();
            }
        });
    }
}