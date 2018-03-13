package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SalesStatusOneDay;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

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

    @Override
    public void loadDailySales(int shopId, long time, CustomObserver<SalesStatusOneDay> observer) {
        RetrofitHttp.getRetrofit().loadDailySales(shopId, time)
                .compose(RxHelper.<BaseEntity<SalesStatusOneDay>>io_main())
                .subscribe(observer);
    }


}
