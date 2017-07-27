package com.lyancafe.coffeeshop.event;

/**
 * Created by Administrator on 2016/7/28.
 */
public class ChangeTabCountByActionEvent {

    public int action;

    public int count;

    public boolean isQrCode;

    public ChangeTabCountByActionEvent(int action, int count, boolean isQrCode) {
        this.action = action;
        this.count = count;
        this.isQrCode = isQrCode;
    }

    public ChangeTabCountByActionEvent(int action, int count) {
        this.action = action;
        this.count = count;
        this.isQrCode = false;
    }
}
