package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.bean.EvaluationBean;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/10.
 */

public class EvaluationListSortComparator implements Comparator<EvaluationBean> {

    @Override
    public int compare(EvaluationBean o1, EvaluationBean o2) {
        return o1.getOrderId()-o2.getOrderId();
    }
}
