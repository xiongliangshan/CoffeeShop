package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.db.OrderUtils;

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
