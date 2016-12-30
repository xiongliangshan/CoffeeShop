package com.lyancafe.coffeeshop.helper;

import com.alibaba.fastjson.JSONObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
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
    public void reqFinishedListData(int orderBy, int fillterInstant, long orderId, JsonCallback<XlsResponse> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId",orderId); //区分是刷新还是加载更多
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
                .headers("isLoadMore", orderId == 0 ? "no" : "yes")
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    /**
     * 请求已完成界面总单量和总杯量
     * @param callback
     */
    public void reqFinishedTotalAmountData(JsonCallback<XlsResponse> callback){
        String url=Urls.BASE_URL+shopId + "/orders/today/finishedTotal?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 按日期查询当天所有订单
     * @param date
     * @param callback
     */
    public void reqSearchOrdersByDate(String date,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/orders/search/day/"+date+"?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 按单号查询某个订单
     * @param orderSn
     * @param callback
     */
    public void reqSearchOrdersByOrderSn(String orderSn,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/orders/search/id/"+orderSn+"?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 物料列表
     * @param callback
     */
    public void reqMaterialList(JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/supplies?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 咖啡师收回从小哥手里收回订单
     * @param orderId
     * @param callback
     */
    public void reqRecallOrder(long orderId,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/order/"+orderId+"/recall?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 请求小哥列表数据
     * @param callback
     */
    public void reqDeliverList(JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/couriersforassign?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 咖啡师指派订单
     * @param orderId
     * @param deliverId
     * @param callback
     */
    public void reqAssignOrder(long orderId,long deliverId,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/order/"+orderId+"/assigntocourier?token="+token;
        HashMap<String, Object> params = new HashMap<>();
        params.put("courierId",deliverId);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);
    }
}
