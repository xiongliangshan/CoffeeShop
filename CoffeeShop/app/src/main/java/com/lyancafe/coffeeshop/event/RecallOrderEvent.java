package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/20.
 */

public class RecallOrderEvent {
    public int tabIndex;
    public int orderId;

    public RecallOrderEvent(int tabIndex, int orderId) {
        this.tabIndex = tabIndex;
        this.orderId = orderId;
    }
}
