package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.shop.model.MaterialsModel;
import com.lyancafe.coffeeshop.shop.model.MaterialsModelImpl;
import com.lyancafe.coffeeshop.shop.view.MaiterialsView;

import java.util.List;

import io.reactivex.Observer;
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
        mMaterialModel.loadMaterials(user.getShopId(), new Observer<BaseEntity<List<Material>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mMaterialView.showContentLoading();
            }

            @Override
            public void onNext(BaseEntity<List<Material>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<Material> materials = listBaseEntity.getData();
                    mMaterialView.bindDataToView(materials);
                }else {
                    Logger.getLogger().log("物料数据接口返回:"+listBaseEntity.getMessage());
                }

            }

            @Override
            public void onError(Throwable e) {
                mMaterialView.dismissContentLoading();
                Logger.getLogger().error("物料数据接口请求失败,"+e.getMessage());
            }

            @Override
            public void onComplete() {
                mMaterialView.dismissContentLoading();
            }
        });
    }
}