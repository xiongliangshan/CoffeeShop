package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface MaterialsModel {

    //加载物料列表
    void loadMaterials(int shopId,CustomObserver<List<Material>> observer);

}