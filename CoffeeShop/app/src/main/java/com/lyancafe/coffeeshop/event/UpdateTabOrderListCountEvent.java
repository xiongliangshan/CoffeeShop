package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/7/28.
 */
public class UpdateTabOrderListCountEvent {

    public int witchTab;
    public int count;

    public UpdateTabOrderListCountEvent(int witchTab, int count) {
        this.witchTab = witchTab;
        this.count = count;
    }
}
