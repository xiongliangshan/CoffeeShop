package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.SalesStatusOneDay;
import com.lyancafe.coffeeshop.widget.PiePercentView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/17.
 */

public interface StatementView extends BaseView{

    void bindData(Map<String,Integer> map);

    void bindPie(List<PiePercentView.PieData> pieDatas);

    void bindDailySales(SalesStatusOneDay salesStatusOneDay);
}
