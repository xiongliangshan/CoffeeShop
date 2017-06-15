package com.lyancafe.coffeeshop.shop.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/5/25.
 */

public class ManagerModelImpl implements ManagerModel {


    @Override
    public void loadShopInfo(int shopId, String token, Observer<BaseEntity<ShopInfo>> observer) {
        RetrofitHttp.getRetrofit().loadShopInfo(shopId,token)
                .compose(RxHelper.<BaseEntity<ShopInfo>>io_main())
                .subscribe(observer);
    }

    @Override
    public void modifyShopTelephone(int shopId, String phoneNubmer, String token, Observer<BaseEntity<JsonObject>> observer) {
        RetrofitHttp.getRetrofit().modifyShopTelephone(shopId,phoneNubmer,token)
                .compose(RxHelper.<BaseEntity<JsonObject>>io_main())
                .subscribe(observer);
    }

}
