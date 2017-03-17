package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/17
*/

public class ShopManagerModelImpl implements ShopManagerModel{


    @Override
    public void loadMaterialList(final OnHandleMaterialListListener listener) {
        HttpHelper.getInstance().reqMaterialList(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.loadMaterialListSuccess(xlsResponse, call, response);
            }
        });
    }

    public interface OnHandleMaterialListListener{
        void loadMaterialListSuccess(XlsResponse xlsResponse, Call call, Response response);
    }
}