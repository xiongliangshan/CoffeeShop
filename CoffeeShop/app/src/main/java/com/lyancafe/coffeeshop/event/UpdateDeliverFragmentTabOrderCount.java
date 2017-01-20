package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2017/1/20.
 */

public class UpdateDeliverFragmentTabOrderCount {
    public int tabIndex;
    public int count;

    public UpdateDeliverFragmentTabOrderCount(int tabIndex, int count) {
        this.tabIndex = tabIndex;
        this.count = count;
    }
}
