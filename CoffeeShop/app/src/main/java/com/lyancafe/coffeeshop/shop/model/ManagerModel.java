package com.lyancafe.coffeeshop.shop.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.ShopInfo;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface ManagerModel {

    /**
     * 加载门店信息
     * @param shopId
     * @param token
     * @param observer
     */
    void loadShopInfo(int shopId, String token, Observer<BaseEntity<ShopInfo>> observer);


    /**
     * 修改门店电话号码
     * @param shopId
     * @param phoneNubmer
     * @param token
     * @param observer
     */
    void modifyShopTelephone(int shopId, String phoneNubmer, String token, Observer<BaseEntity<JsonObject>> observer);
}
