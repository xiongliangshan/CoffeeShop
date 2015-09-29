package com.xls.http;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/31.
 */
public class Jresp {
    private static final String TAG = "Jresp";

    public int status;   //返回的状态码
    public String message; //返回的文字消息
    public JSONObject data;    //返回的数据


    public Jresp(int status, String message, JSONObject data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Jresp{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
