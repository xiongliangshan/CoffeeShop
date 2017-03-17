package com.lyancafe.coffeeshop.shop.model;

/**
* Created by Administrator on 2017/03/17
*/

public interface ShopManagerModel{

    //加载物料列表
    void loadMaterialList(ShopManagerModelImpl.OnHandleMaterialListListener listListener);

}