package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.EvaluationBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface EvaluationView<T> extends BaseView{

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

    //追加列表数据
    void appendListData(List<T> list);

    //保存lastFeedbackId
    void saveLastFeedbackId();

    //停止加载进度对话框
    void stopLoadingProgress();

    /*//绑定各类型评价数量
    void bindEvaluationAmount(int positive,int negative);*/
}