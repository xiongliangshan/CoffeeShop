package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.db.OrderUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class RevokedModelImpl implements RevokedModel {

    @Override
    public List<OrderBean> loadRevokedOrders() {
        return OrderUtils.with().queryRevokedOrders();
    }
}
