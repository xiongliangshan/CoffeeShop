package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/7/28.
 */
public class CommitIssueOrderEvent {

    public long orderId;

    public CommitIssueOrderEvent(long orderId) {
        this.orderId = orderId;
    }
}
