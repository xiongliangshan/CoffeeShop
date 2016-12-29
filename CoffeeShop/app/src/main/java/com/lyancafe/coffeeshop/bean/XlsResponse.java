package com.lyancafe.coffeeshop.bean;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/12/29.
 */
public class XlsResponse{
    public int status;
    public String message;
    public JSONObject data;


    @Override
    public String toString() {
        return "XlsResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
