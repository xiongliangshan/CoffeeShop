package com.lyancafe.coffeeshop.helper;

import com.alibaba.fastjson.JSONObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/28.
 * 所有接口
 */
public class HttpHelper {
    private static HttpHelper http;
    private int shopId;
    private String token;

    public HttpHelper(int shopId, String token) {
        this.shopId = shopId;
        this.token = token;
    }

    public static HttpHelper getInstance(){
        if(http==null){
            LoginBean loginBean = LoginHelper.getLoginBean(CSApplication.getInstance());
            int shopId = loginBean.getShopId();
            String token = loginBean.getToken();
            return new HttpHelper(shopId,token);
        }
        return http;
    }


    /**
     * 请求待生产列表数据
     * @param orderBy
     * @param fillterInstant
     * @param limitLevel
     * @param callback
     */
    public void reqToProduceData(int orderBy, int fillterInstant,int limitLevel,JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderBy", orderBy);
        params.put("fillterInstant", fillterInstant);
        params.put("limitLevel", limitLevel);
        JSONObject jsonObject = new JSONObject(params);

        String url=null;
        if(LoginHelper.isSFMode()){
            url = Urls.BASE_URL+shopId + "/orders/today/toproduce/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL + shopId + "/orders/today/toproduce?token="+token;
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    /**
     * 请求生产中列表数据
     * @param orderBy
     * @param fillterInstant
     * @param callback
     */
    public void reqProducingData(int orderBy, int fillterInstant,JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderBy", orderBy);
        params.put("fillterInstant", fillterInstant);
        JSONObject jsonObject = new JSONObject(params);

        String url=null;
        if(LoginHelper.isSFMode()){
            url = Urls.BASE_URL+shopId + "/orders/today/producing/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL + shopId + "/orders/today/producing?token="+token;
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    /**
     * 请求已生产列表数据
     * @param orderBy
     * @param fillterInstant
     * @param callback
     */
    public void reqProducedData(int orderBy, int fillterInstant,JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderBy", orderBy);
        params.put("fillterInstant", fillterInstant);
        JSONObject jsonObject = new JSONObject(params);

        String url=null;
        if(LoginHelper.isSFMode()){
            url = Urls.BASE_URL+shopId + "/orders/today/produced/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL + shopId + "/orders/today/produced?token="+token;
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);

    }


    /**
     * 请求配送中列表数据
     * @param orderBy
     * @param fillterInstant
     * @param callback
     */
    public void reqDeliveryingData(int orderBy, int fillterInstant,JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderBy", orderBy);
        params.put("fillterInstant", fillterInstant);
        JSONObject jsonObject = new JSONObject(params);

        String url=null;
        if(LoginHelper.isSFMode()){
            url = Urls.BASE_URL+shopId + "/orders/today/delivering/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL + shopId + "/orders/today/delivering?token="+token;
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    /**
     * 请求已完成列表数据
     * @param orderBy
     * @param fillterInstant
     * @param callback
     */
    public void reqFinishedData(int orderBy, int fillterInstant,JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderBy", orderBy);
        params.put("fillterInstant", fillterInstant);
        JSONObject jsonObject = new JSONObject(params);

        String url=null;
        if(LoginHelper.isSFMode()){
            url = Urls.BASE_URL+shopId + "/orders/today/finished/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL + shopId + "/orders/today/finished?token="+token;
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    public void reqFinishedTotalAmountData(JsonCallback<XlsResponse> callback){
        String url=Urls.BASE_URL+shopId + "/orders/today/finishedTotal?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }
}
