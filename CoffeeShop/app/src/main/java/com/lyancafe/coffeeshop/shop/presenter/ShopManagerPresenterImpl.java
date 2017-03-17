package com.lyancafe.coffeeshop.shop.presenter;


import android.content.Context;

import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.shop.model.ShopManagerModel;
import com.lyancafe.coffeeshop.shop.model.ShopManagerModelImpl;
import com.lyancafe.coffeeshop.shop.view.ShopManagerView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class ShopManagerPresenterImpl implements ShopManagerPresenter,ShopManagerModelImpl.OnHandleMaterialListListener{

    private Context mContext;
    private ShopManagerModel mShopManagerModel;
    private ShopManagerView mShopManagerView;

    public ShopManagerPresenterImpl(Context mContext, ShopManagerView mShopManagerView) {
        this.mContext = mContext;
        this.mShopManagerView = mShopManagerView;
        mShopManagerModel = new ShopManagerModelImpl();
    }

    @Override
    public void loadMaterialList() {
        mShopManagerModel.loadMaterialList(this);
    }

    @Override
    public void loadMaterialListSuccess(XlsResponse xlsResponse, Call call, Response response) {
        if (xlsResponse.status == 0) {
            List<MaterialBean> materialList = MaterialBean.parseJsonMaterials(mContext, xlsResponse);
            mShopManagerView.bindDataToListView(materialList);
        } else {
            mShopManagerView.showToast(xlsResponse.message);
        }
    }
}