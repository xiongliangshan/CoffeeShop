package com.lyancafe.coffeeshop.shop.model;


import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.http.RetrofitHttp;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
* Created by Administrator on 2017/03/17
*/

public class MaterialsModelImpl implements MaterialsModel {


    @Override
    public void loadMaterials(int shopId, String token, Observer<BaseEntity<List<Material>>> observer) {
        RetrofitHttp.getRetrofit().loadMaterials(shopId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}