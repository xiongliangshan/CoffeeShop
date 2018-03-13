package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SalesStatusOneDay;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.http.Api;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.shop.model.StatementModel;
import com.lyancafe.coffeeshop.shop.model.StatementModelImpl;
import com.lyancafe.coffeeshop.shop.view.StatementView;
import com.lyancafe.coffeeshop.widget.PiePercentView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        statementView.bindPie(calculateEffect(finishedOrders));
        return map;
    }


    @Override
    public List<PiePercentView.PieData> calculateEffect(List<OrderBean> finishedOrders) {
        List<PiePercentView.PieData> pieDatas = new ArrayList<>();
        int good = 0;
        int pass = 0;
        int bad = 0;
        for(OrderBean order:finishedOrders){
            String effect = OrderHelper.getRealTimeToService(order);
            switch (effect){
                case "良好":
                    good++;
                    break;
                case "合格":
                    pass++;
                    break;
                case "不及格":
                    bad++;
                    break;
            }
        }
        if(good>0){
            pieDatas.add(new PiePercentView.PieData("良好",good,0xFFCCFF00));
        }
        if(pass>0){
            pieDatas.add(new PiePercentView.PieData("合格",pass,0xFF6495ED));
        }
        if(bad>0){
            pieDatas.add(new PiePercentView.PieData("不及格",bad,0xFFE32636));
        }
        return pieDatas;
    }

    @Override
    public void getDailySales(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date timeS = cal.getTime();
        try {
            if( null == time ){
                time = new SimpleDateFormat("yyyy-MM-dd").format(timeS);
            }else if("".equals(time)){
                time = new SimpleDateFormat("yyyy-MM-dd").format(timeS);
            }
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            Logger.getLogger().log("daily sales status error ,from time exchange,time=" + time);
        }
        UserBean user = LoginHelper.getUser(context.getApplicationContext());
        Api.changeBaseUrl();
        statementModel.loadDailySales(user.getShopId(), date.getTime(), new CustomObserver<SalesStatusOneDay>(context) {
            @Override
            protected void onHandleSuccess(SalesStatusOneDay salesStatusOneDay) {
                statementView.bindDailySales(salesStatusOneDay);
            }

            @Override

            public void onError(Throwable e) {
                super.onError(e);
                Api.initBaseUrl();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                Api.initBaseUrl();
            }
        });
    }
}
