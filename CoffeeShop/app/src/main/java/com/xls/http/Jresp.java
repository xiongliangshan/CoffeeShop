package com.xls.http;

/**
 * Created by Administrator on 2015/8/31.
 */
public class Jresp {
    private static final String TAG = "Jresp";

    public int response;   //返回的状态码
    public String message; //返回的文字消息
    public String data;    //返回的数据


    public Jresp(int response, String message, String data) {
        this.response = response;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Jresp{" +
                "response=" + response +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
