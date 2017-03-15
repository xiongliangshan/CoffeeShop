package com.lyancafe.coffeeshop.helper;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.RsaEncryptor;
import com.lyancafe.coffeeshop.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/12/28.
 * 所有接口
 */
public class HttpHelper {
    private static HttpHelper http;
    private int shopId;
    private int userId;
    private String token;

    public HttpHelper(int shopId, int userId, String token) {
        this.shopId = shopId;
        this.userId = userId;
        this.token = token;
    }

    public static HttpHelper getInstance(){
        if(http==null){
            UserBean userBean = LoginHelper.getLoginBean(CSApplication.getInstance());
            int shopId = userBean.getShopId();
            int userId = userBean.getUserId();
            String token = userBean.getToken();
            return new HttpHelper(shopId,userId,token);
        }
        return http;
    }


    /**
     * 登录接口
     * @param userName
     * @param password
     * @param callback
     */
    public void reqLogin(String userName,String password,JsonCallback<XlsResponse> callback){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loginName",userName);
        RsaEncryptor rsa = null;
        String enc_pwd = "";
        try {
            rsa = new RsaEncryptor(CSApplication.getInstance(), "public.key");
            enc_pwd = rsa.encrypt(password);
        } catch (Exception e) {
            Log.d("login", e.getMessage());
        }
        params.put("password", enc_pwd);
        JSONObject jsonObject = new JSONObject(params);
        String url = Urls.BASE_URL+"token";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);
    }

    /**
     * 上传设备信息，主要是推送的registrationId。
     * @param regId
     * @param callback
     */
    public void reqUploadDeviceInfo(String regId,JsonCallback<XlsResponse> callback){
        String deviceId = "";
        String mType = android.os.Build.MODEL; // 手机型号
        int appCode = MyUtil.getVersionCode(CSApplication.getInstance());
        if(TextUtils.isEmpty(regId)){
            JPushInterface.init(CSApplication.getInstance());
            return;
        }
        if(userId==0){
            return;
        }
        String url = Urls.BASE_URL+shopId+"/barista/"+userId+"/device?token="+token;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("deviceId",deviceId);
        params.put("mType",mType);
        params.put("appCode",appCode);
        params.put("regId", regId);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);
        Log.d("upload", "upload device info:" + deviceId + "|" + mType + "|" + appCode + "|" + regId);
    }


    /**
     * 请求待生产列表数据
     * @param callback
     */
    public void reqToProduceData(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求待生产列表");
        String url = Urls.BASE_URL + shopId + "/orders/today/toproduce?token="+token;;
        OkGo.post(url)
                .tag(this)
                .execute(callback);

    }

    /**
     * 请求生产中列表数据
     * @param callback
     */
    public void reqProducingData(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求生产中列表");
        String url = Urls.BASE_URL + shopId + "/orders/today/producing?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);

    }

    /**
     * 请求已生产列表数据
     * @param callback
     */
    public void reqProducedData(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求待取货列表");
        String url = Urls.BASE_URL + shopId + "/orders/today/produced?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);

    }


    /**
     * 请求配送中列表数据
     * @param callback
     */
    public void reqDeliveryingData(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求配送中列表");
        String url = Urls.BASE_URL + shopId + "/orders/today/delivering?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);

    }

    /**
     * 请求已完成列表数据
     * @param callback
     */
    public void reqFinishedListData(long orderId, JsonCallback<XlsResponse> callback){
        Log.d("xls","请求订单一览列表数据");
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId",orderId); //区分是刷新还是加载更多
        JSONObject jsonObject = new JSONObject(params);

        String url = Urls.BASE_URL + shopId + "/orders/today/finished?token="+token;
        OkGo.post(url)
                .tag("finished")
                .headers("isLoadMore", orderId == 0 ? "no" : "yes")
                .upJson(jsonObject.toString())
                .execute(callback);

    }

    /**
     * 请求已完成界面总单量和总杯量
     * @param callback
     */
    public void reqFinishedTotalAmountData(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求总杯量和总单量");
        String url=Urls.BASE_URL+shopId + "/orders/today/finishedTotal?token="+token;
        OkGo.get(url)
                .tag("finished")
                .execute(callback);
    }

    /**
     * 请求服务时效的各阶段订单所占百分比
     * @param callback
     */
    public void reqServiceEffectPersent(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求服务时效百分比");
        String url=Urls.BASE_URL+shopId + "/orders/scaleInfo?token="+token;
        OkGo.get(url)
                .tag("finished")
                .execute(callback);
    }

    /**
     * 请求评价列表
     * @param orderId
     * @param type
     * @param callback
     */
    public void reqEvaluationListData(int orderId,int type,JsonCallback<XlsResponse> callback){
        HashMap<String,Object> params = new HashMap<>();
        params.put("orderId",orderId);
        params.put("feedbackType",type);
        JSONObject jsonObject = new JSONObject(params);
        String url=Urls.BASE_URL+shopId + "/orders/feedbackList?token="+token;
        OkGo.post(url)
                .tag("evaluation")
                .headers("isLoadMore", orderId == 0 ? "no" : "yes")
                .upJson(jsonObject.toString())
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
        Log.d("xls","请求物料");
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

    /**
     * 开始生产
     * @param orderId
     * @param callback
     */
    public void reqStartProduce(long orderId, DialogCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/order/"+orderId+"/beginproduce?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }


    /**
     * 生产完成
     * @param orderId
     * @param callback
     */
    public void reqFinishedProduce(long orderId,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/order/"+orderId+"/produce?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 评论数量接口
     * @param callback
     */
    public void reqCommentCount(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求评论列表");
        String url = Urls.BASE_URL+shopId+"/orders/feedback/count?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }


    /**
     * 请求时效列表
     * @param lastOrderId
     * @param levelType
     * @param callback
     */
    public void reqTimeEffectList(int lastOrderId,int levelType,JsonCallback<XlsResponse> callback){
        Log.d("xls","请求时效列表");
        HashMap<String,Object> params = new HashMap<>();
        params.put("orderId",lastOrderId);
        params.put("levelType",levelType);
        JSONObject jsonObject = new JSONObject(params);
        String url=Urls.BASE_URL+shopId + "/orders/commissionList?token="+token;
        OkGo.post(url)
                .tag("timeEffect")
                .headers("isLoadMore", lastOrderId == 0 ? "no" : "yes")
                .upJson(jsonObject.toString())
                .execute(callback);
    }

    /**
     * 请求各类时效订单的数量
     * @param callback
     */
    public void reqTimeEffectTypeCount(JsonCallback<XlsResponse> callback){
        Log.d("xls","请求时效数量");
        String url = Urls.BASE_URL+shopId+"/orders/commissionListCount?token="+token;
        OkGo.get(url)
                .tag("timeEffect")
                .execute(callback);
    }


    /**
     * 检测版本更新
     * @param callback
     */
    public void reqCheckUpdate(JsonCallback<XlsResponse> callback){
        int curVersion = MyUtil.getVersionCode(CSApplication.getInstance());
        String url = Urls.BASE_URL + "/token/"+curVersion+"/isUpdateApp?token="+token;
        OkGo.post(url)
                .tag(this)
                .execute(callback);
    }

    /**
     * 退出登录接口
     * @param callback
     */
    public void reqLoginOut(JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+"/token/delete?token="+token;
        OkGo.get(url)
                .tag(this)
                .execute(callback);
    }


    /**
     * 上报问题订单
     * @param orderId
     * @param questionType 14.产线繁忙 15.无法生产 16.定位错误，应属其他区域 17.其他
     * @param questionIdea 1.取消订单 2.改约时间
     * @param questionDesc
     * @param callback
     */

    public void reqReportIssueOrder(long orderId, int questionType, int questionIdea, String questionDesc,JsonCallback<XlsResponse> callback){
        String url = Urls.BASE_URL+shopId+"/order/"+orderId+"/raiseissue?token="+token;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("questionType",questionType);
        params.put("handleType",questionIdea);
        params.put("remark", questionDesc);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(callback);
    }

}
