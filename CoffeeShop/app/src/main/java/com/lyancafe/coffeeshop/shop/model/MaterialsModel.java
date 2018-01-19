package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;

import java.util.List;

import io.reactivex.Observer;

/**
* Created by Administrator on 2017/03/17
*/

public interface MaterialsModel {

    //加载物料列表
    void loadMaterials(int shopId,Observer<BaseEntity<List<Material>>> observer);

}