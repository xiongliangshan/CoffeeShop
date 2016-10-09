package com.lyancafe.coffeeshop.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */
public class ApkInfoBean implements Serializable{

    private String appName;
    private int appNo;
    private String appMd5;
    private String url;

    public ApkInfoBean() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppNo() {
        return appNo;
    }

    public void setAppNo(int appNo) {
        this.appNo = appNo;
    }

    public String getAppMd5() {
        return appMd5;
    }

    public void setAppMd5(String appMd5) {
        this.appMd5 = appMd5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ApkInfoBean{" +
                "appName='" + appName + '\'' +
                ", appNo=" + appNo +
                ", appMd5='" + appMd5 + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public static ApkInfoBean parseJsonToBean(String jsonStr){
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        ApkInfoBean apkInfoBean = JSON.parseObject(jsonStr, ApkInfoBean.class);
        return apkInfoBean;
    }
}
