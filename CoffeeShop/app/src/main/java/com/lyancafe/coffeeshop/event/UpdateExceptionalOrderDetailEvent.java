package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.ExceptionalOrder;

/**
 * Created by Administrator on 2016/7/29.
 */
public class UpdateExceptionalOrderDetailEvent {

    public ExceptionalOrder exceptionalOrder;

    public UpdateExceptionalOrderDetailEvent(ExceptionalOrder exceptionalOrder) {
        this.exceptionalOrder = exceptionalOrder;
    }
}
