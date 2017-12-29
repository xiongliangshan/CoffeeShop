package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public class MaterialsModelImpl implements MaterialsModel {


    @Override
    public void loadMaterials(int shopId, CustomObserver<List<Material>> observer) {
        RetrofitHttp.getRetrofit().loadMaterials(shopId)
                .compose(RxHelper.<BaseEntity<List<Material>>>io_main())
                .subscribe(observer);
    }
}