package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.MaterialBean;

/**
* Created by Administrator on 2017/03/17
*/

public interface MaiterialsView extends BaseView<Material>{

    void showLoading();

    void dismissLoading();
}