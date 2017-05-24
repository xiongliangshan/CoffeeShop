package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.shop.model.MaterialsModel;
import com.lyancafe.coffeeshop.shop.model.MaterialsModelImpl;
import com.lyancafe.coffeeshop.shop.view.MaiterialsView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.List;

import io.reactivex.Observer;
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
        UserBean userBean = LoginHelper.getUser(CSApplication.getInstance());
        int shopId = userBean.getShopId();
        String token = userBean.getToken();
        mMaterialModel.loadMaterials(shopId, token, new Observer<BaseEntity<List<Material>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mMaterialView.showLoading();
            }

            @Override
            public void onNext(@NonNull BaseEntity<List<Material>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<Material> materials = listBaseEntity.getData();
                    mMaterialView.bindDataToListView(materials);
                }else{
                    ToastUtil.showToast(mContext.getApplicationContext(),listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mMaterialView.dismissLoading();
                ToastUtil.showToast(mContext.getApplicationContext(),e.getMessage());
            }

            @Override
            public void onComplete() {
                mMaterialView.dismissLoading();
            }
        });
    }
}