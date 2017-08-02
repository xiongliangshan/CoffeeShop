package com.lyancafe.coffeeshop.produce.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/7/28.
 */

public class TomorrowModelImpl implements TomorrowModel {

    @Override
    public void loadTomorrowOrders(int shopId, String token, Observer<BaseEntity<List<OrderBean>>> observer) {
        RetrofitHttp.getRetrofit().loadTomorrowOrders(shopId,token)
                .compose(RxHelper.<BaseEntity<List<OrderBean>>>io_main())
                .subscribe(observer);
    }
}

