package com.xls.http;

import java.util.Map;

/**
 * Created by Administrator on 2015/8/31.
 */
public class HttpEntity {

    private static final String TAG = "HttpEntity";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "delete";

    private String method;
    private String url;
    private Map<String, Object> params;

    public HttpEntity(String method, String url, Map<String, Object> params) {
        this.method = method;
        this.url = url;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    @Override
    public String toString() {
        return "HttpEntity{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", params=" + params +
                '}';
    }
}
