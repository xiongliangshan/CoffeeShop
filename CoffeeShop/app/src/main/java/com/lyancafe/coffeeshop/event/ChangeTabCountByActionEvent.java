package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/7/28.
 */
public class ChangeTabCountByActionEvent {

    public int action;

    public int count;

    public ChangeTabCountByActionEvent(int action, int count) {
        this.action = action;
        this.count = count;
    }
}
