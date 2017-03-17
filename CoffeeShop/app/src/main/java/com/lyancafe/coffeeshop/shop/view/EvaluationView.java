package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.EvaluationBean;

import java.util.List;

/**
* Created by Administrator on 2017/03/17
*/

public interface EvaluationView extends BaseView<EvaluationBean>{

    //追加列表数据
    void appendListData(List<EvaluationBean> list);

    //保存lastOrderId
    void saveLastOrderId();

    //停止加载进度对话框
    void stopLoadingProgress();

    //绑定各类型评价数量
    void bindEvaluationAmount(int positive,int negative);
}