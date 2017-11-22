package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.ShopInfo;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface ManagerView extends BaseView {

    void showToast(String message);

    /*void showLoading();

    void dismissLoading();*/

    void bindShopInfoDataToView(ShopInfo shopInfo);

    void showEdit();

    void hideEdit();

    void setTelephone(String phone);
}
