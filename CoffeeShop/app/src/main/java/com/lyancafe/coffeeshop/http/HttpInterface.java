package com.lyancafe.coffeeshop.http;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.BatchOrder;
import com.lyancafe.coffeeshop.bean.DeliverBean;
import com.lyancafe.coffeeshop.bean.DeviceGroup;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.bean.VideoBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/3/30.
 */

public interface HttpInterface {


    /**
     * 登录
     * @param loginName 账户
     * @param password icon_password
     * @return
     */
    @POST("v3/token")
    Observable<BaseEntity<UserBean>> login(@QueryMap Map<String,Object> params);




    /**
     * 待生产订单列表
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/today/toproduce/orders")
    Observable<BaseEntity<List<OrderBean>>> loadToProduceOrders(@Path("shopId") int shopId);


    /**
     * 开始生产
     * @param shopId
     * @param orderId
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/beginproduce")
    Observable<BaseEntity<JsonObject>> doStartProduce(@Path("shopId") int shopId, @Path("orderId") long orderId);

    /**
     * 针对补单的无需操作
     * @param shopId
     * @param orderId
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/needlessProduce")
    Observable<BaseEntity<JsonObject>> doNoProduce(@Path("shopId") int shopId, @Path("orderId") long orderId);


    /**
     * 批量生产接口
     * @param shopId
     * @return
     */
    @POST("v3/{shopId}/order/batchBeginProduce")
    Observable<BaseEntity<JsonObject>> doStartBatchProduce(@Path("shopId") int shopId, @Body BatchOrder batchOrder);

    /**
     * 生产中订单列表
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/today/producing/orders")
    Observable<BaseEntity<List<OrderBean>>> loadProducingOrders(@Path("shopId") int shopId);

    /**
     * 生产完成
     * @param shopId
     * @param orderId
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/produce")
    Observable<BaseEntity<JsonObject>> doFinishProduced(@Path("shopId") int shopId, @Path("orderId") long orderId);


    /**
     * 批量完成生产
     * @param shopId
     * @param orderIds
     * @return
     */
    @POST("v3/{shopId}/order/batchCompleteProduce")
    Observable<BaseEntity<JsonObject>> doCompleteBatchProduce(@Path("shopId") int shopId,@Query("orderIds") List<Long> orderIds);


    /**
     * 已生产列表
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/today/produced/orders")
    Observable<BaseEntity<List<OrderBean>>> loadToFetchOrders(@Path("shopId") int shopId);


    /**
     * 明日订单列表
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/orders/tomorrow")
    Observable<BaseEntity<List<OrderBean>>> loadTomorrowOrders(@Path("shopId") int shopId);


    /**
     * 已完成订单列表
     * @param shopId
     * @param orderId
     * @return
     */
    @GET("v3/{shopId}/orders/today/finished")
    Observable<BaseEntity<List<OrderBean>>> loadFinishedOrders(@Path("shopId") int shopId,@Query("orderId") long orderId);


    /**
     * 已完成订单单量和杯量
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/orders/today/finishedTotal")
    Observable<BaseEntity<JsonObject>> loadOrderAmount(@Path("shopId") int shopId);


    /**
     * 评价列表
     * @param shopId
     * @param id
     * @return
     */
    @GET("v3/{shopId}/orders/feedbackList")
    Observable<BaseEntity<List<EvaluationBean>>> loadEvaluations(@Path("shopId") int shopId,@Query("id") int id);



    /**
     * 加载物料列表内容
     * @param shopId
     * @return
     */
    @Headers("Cache-Control:public,max-age=60,max-stale=1200")
    @GET("v3/{shopId}/supplies")
    Observable<BaseEntity<List<Material>>> loadMaterials(@Path("shopId") int shopId);


    /**
     * 检测版本更新
     * @param curVersion
     * @return
     */
    @POST("v3/token/{curVersion}/isUpdateApp")
    Observable<BaseEntity<ApkInfoBean>> checkUpdate(@Path("curVersion") int curVersion);


    /**
     * 退出登录
     * @return
     */
    @POST("v3/token/delete")
    Observable<BaseEntity> exitLogin();


    /**
     * 问题订单反馈
     * @param shopId
     * @param orderId
     * @param params
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/raiseissue")
    Observable<BaseEntity> reportIssue(@Path("shopId") int shopId,@Path("orderId") long orderId,@QueryMap Map<String,Object> params);


    /**
     * 可以指派的小哥列表
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/couriersforassign")
    Observable<BaseEntity<List<DeliverBean>>> loadDeliversForAssign(@Path("shopId") int shopId);


    /**
     * 指派订单
     * @param shopId
     * @param orderId
     * @param courierId
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/assigntocourier")
    Observable<BaseEntity<JsonObject>> doAssignOrder(@Path("shopId") int shopId,@Path("orderId") long orderId,@Query("courierId") long courierId);

    /**
     * 订单从小哥手中撤回
     * @param shopId
     * @param orderId
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/recall")
    Observable<BaseEntity<JsonObject>> doRecallOrder(@Path("shopId") int shopId,@Path("orderId") long orderId);


    /**
     * 获取门店基本信息
     * @param shopId
     * @return
     */
    @Headers("Cache-Control:public,max-age=1,max-stale=2")
    @GET("v3/{shopId}/shop/info")
    Observable<BaseEntity<ShopInfo>> loadShopInfo(@Path("shopId") int shopId);

    /**
     * 修改门店电话号码
     * @param shopId
     * @param shopTelephone
     * @return
     */
    @POST("v3/{shopId}/shop/update")
    Observable<BaseEntity<JsonObject>> modifyShopTelephone(@Path("shopId") int shopId,@Query("shopTelephone") String shopTelephone);

    /**
     * 获取异常订单
     * @param shopId
     * @return
     */
    @GET("v3/{shopId}/orders/today/haikuiTimeOutOrders")
    Observable<BaseEntity<List<ExceptionalOrder>>> loadExceptionalOrders(@Path("shopId") int shopId);

    /**
     * 重新派发订单给指定配送团队
     * @param shopId
     * @param orderId
     * @param team
     * @return
     */
    @POST("v3/{shopId}/order/{orderId}/{deliveryTeam}/rePushOrderToDelivery")
    Observable<BaseEntity<JsonObject>> doRePush(@Path("shopId") int shopId,@Path("orderId") long orderId,@Path("deliveryTeam") int team);


    /**
     * 加载视频列表数据
     * @param shopId
     * @return
     */

    @Headers("Cache-Control:public,max-age=60,max-stale=300")
    @GET("v3/{shopId}/shop/videolist")
    Observable<BaseEntity<List<VideoBean>>> loadVideos(@Path("shopId") int shopId);


    /**
     * 设备监控，心跳请求
     * @param data
     * @return
     */
    @POST("device/heartbeat")
    Observable<BaseEntity<JsonObject>>heartbeat(@Body DeviceGroup data);


    /**
     * 上传日志文件
     * @return
     */
    @Multipart
    @POST("log/upload")
    Observable<BaseEntity<JsonObject>> uploadFile(@Part("shopId") RequestBody shopId,@Part MultipartBody.Part file);

}
