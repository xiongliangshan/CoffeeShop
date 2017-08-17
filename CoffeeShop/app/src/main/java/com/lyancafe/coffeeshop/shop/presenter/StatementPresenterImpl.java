package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.shop.model.StatementModel;
import com.lyancafe.coffeeshop.shop.model.StatementModelImpl;
import com.lyancafe.coffeeshop.shop.view.StatementView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/17.
 */

public class StatementPresenterImpl implements StatementPresenter {


    private Context context;
    private StatementView statementView;
    private StatementModel statementModel;

    public StatementPresenterImpl(Context context, StatementView statementView) {
        this.context = context;
        this.statementView = statementView;
        statementModel = new StatementModelImpl();
    }

    @Override
    public Map<String, Integer> calculateCount() {
        Map<String ,Integer> map = new HashMap<>();
        List<OrderBean> allOrders =   statementModel.loadAllCacheOrders();
        List<OrderBean> finishedOrders = statementModel.loadFinishedOrders();
        map.put("finishedOrders",finishedOrders.size());
        map.put("allOrders",allOrders.size());
        int totalCupCount = OrderHelper.getTotalQutity(allOrders);
        int finishedCupCount = OrderHelper.getTotalQutity(finishedOrders);
        map.put("finishedCups",finishedCupCount);
        map.put("allCups",totalCupCount);
        statementView.bindData(map);
        return map;
    }
}
