package com.lyancafe.coffeeshop.shop.model;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.shop.view.StatementView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class StatementModelImpl implements StatementModel {


    @Override
    public List<OrderBean> loadAllCacheOrders() {
        return OrderUtils.with().queryAllOrders();

    }

    @Override
    public List<OrderBean> loadFinishedOrders() {
        return OrderUtils.with().queryFinishedOrders();
    }
}
