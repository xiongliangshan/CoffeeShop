package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/20.
 */

public class RecallOrderEvent {
    public XlsResponse xlsResponse;
    public Call call;
    public Response response;

    public RecallOrderEvent(XlsResponse xlsResponse, Call call, Response response) {
        this.xlsResponse = xlsResponse;
        this.call = call;
        this.response = response;
    }
}
