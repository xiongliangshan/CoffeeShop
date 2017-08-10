package com.lyancafe.coffeeshop.shop.model;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.http.CustomObserver;

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
    void loadShopInfo(int shopId, String token, CustomObserver<ShopInfo> observer);


    /**
     * 修改门店电话号码
     * @param shopId
     * @param phoneNubmer
     * @param token
     * @param observer
     */
    void modifyShopTelephone(int shopId, String phoneNubmer, String token, CustomObserver<JsonObject> observer);
}
