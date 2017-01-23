package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2017/1/20.
 */

public class UpdateProduceFragmentTabOrderCount {
    public int tabIndex;
    public int count;

    public UpdateProduceFragmentTabOrderCount(int tabIndex, int count) {
        this.tabIndex = tabIndex;
        this.count = count;
    }
}
