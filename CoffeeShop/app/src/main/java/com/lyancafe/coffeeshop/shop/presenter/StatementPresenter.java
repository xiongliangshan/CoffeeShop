package com.lyancafe.coffeeshop.shop.presenter;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.widget.PiePercentView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/17.
 */

public interface StatementPresenter {


    Map<String,Integer> calculateCount();

    List<PiePercentView.PieData> calculateEffect(List<OrderBean> finishedOrders);
}
