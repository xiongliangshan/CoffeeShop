package com.lyancafe.coffeeshop.http;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.DeliverBean;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
     * @param password 密码
     * @return
     */
    @POST("token")
    Observable<BaseEntity<UserBean>> login(@Query("loginName") String loginName,@Query("password") String password);


    /**
     * 上传设备
     * @param shopId 门店Id
     * @param userId 咖啡师Id
     * @param params 设备型号，APP版本号，regId
     * @return
     */
    @POST("{shopId}/barista/{userId}/device")
    Observable<BaseEntity> uploadDeviceInfo(@Path("shopId") int shopId,@Path("userId") int userId,@QueryMap Map<String,Object> params);


    /**
     * 待生产订单列表
     * @param shopId
     * @param token
     * @return
     */
    @POST("{shopId}/orders/today/toproduce")
    Observable<BaseEntity<List<OrderBean>>> loadToProduceOrders(@Path("shopId") int shopId,@Query("token") String token);


    /**
     * 开始生产
     * @param shopId
     * @param orderId
     * @param token
     * @return
     */
    @GET("{shopId}/order/{orderId}/beginproduce")
    Observable<BaseEntity<JsonObject>> doStartProduce(@Path("shopId") int shopId, @Path("orderId") long orderId, @Query("token") String token);


    /**
     * 生产中订单列表
     * @param shopId
     * @param token
     * @return
     */
    @POST("{shopId}/orders/today/producing")
    Observable<BaseEntity<List<OrderBean>>> loadProducingOrders(@Path("shopId") int shopId,@Query("token") String token);

    /**
     * 生产完成
     * @param shopId
     * @param orderId
     * @param token
     * @return
     */
    @GET("{shopId}/order/{orderId}/produce")
    Observable<BaseEntity<JsonObject>> doFinishProduced(@Path("shopId") int shopId, @Path("orderId") long orderId, @Query("token") String token);



    /**
     * 待取货列表
     * @param shopId
     * @param token
     * @return
     */
    @POST("{shopId}/orders/today/produced")
    Observable<BaseEntity<List<OrderBean>>> loadToFetchOrders(@Path("shopId") int shopId,@Query("token") String token);

    /**
     * 配送中列表
     * @param shopId
     * @param token
     * @return
     */
    @POST("{shopId}/orders/today/delivering")
    Observable<BaseEntity<List<OrderBean>>> loadDeliveringOrders(@Path("shopId") int shopId,@Query("token") String token);


    /**
     * 已完成订单列表
     * @param shopId
     * @param orderId
     * @param token
     * @return
     */
    @POST("{shopId}/orders/today/finished")
    Observable<BaseEntity<List<OrderBean>>> loadFinishedOrders(@Path("shopId") int shopId,@Query("orderId") long orderId,@Query("token") String token);


    /**
     * 已完成订单单量和杯量
     * @param shopId
     * @param token
     * @return
     */
    @GET("{shopId}/orders/today/finishedTotal")
    Observable<BaseEntity<JsonObject>> loadOrderAmount(@Path("shopId") int shopId,@Query("token") String token);


    /**
     * 评价列表
     * @param shopId
     * @param orderId
     * @param feedbackType
     * @param token
     * @return
     */
    @POST("{shopId}/orders/feedbackList")
    Observable<BaseEntity<List<EvaluationBean>>> loadEvaluations(@Path("shopId") int shopId,@Query("orderId") long orderId,
                                                                 @Query("feedbackType") int feedbackType,@Query("token") String token);



    /**
     * 加载物料列表内容
     * @param shopId
     * @param token
     * @return
     */
    @Headers("Cache-Control:public,max-age=60,max-stale=1200")
    @GET("{shopId}/supplies")
    Observable<BaseEntity<List<Material>>> loadMaterials(@Path("shopId") int shopId, @Query("token") String token);


    /**
     * 检测版本更新
     * @param curVersion
     * @return
     */
    @POST("token/{curVersion}/isUpdateApp")
    Observable<BaseEntity<ApkInfoBean>> checkUpdate(@Path("curVersion") int curVersion);


    /**
     * 退出登录
     * @param token
     * @return
     */
    @GET("token/delete")
    Observable<BaseEntity> exitLogin(@Query("token") String token);


    /**
     * 问题订单反馈
     * @param shopId
     * @param orderId
     * @param params
     * @return
     */
    @POST("{shopId}/order/{orderId}/raiseissue")
    Observable<BaseEntity> reportIssue(@Path("shopId") int shopId,@Path("orderId") long orderId,@QueryMap Map<String,Object> params);


    /**
     * 可以指派的小哥列表
     * @param shopId
     * @param token
     * @return
     */
    @GET("{shopId}/couriersforassign")
    Observable<BaseEntity<List<DeliverBean>>> loadDeliversForAssign(@Path("shopId") int shopId,@Query("token") String token);


    /**
     * 指派订单
     * @param shopId
     * @param orderId
     * @param courierId
     * @param token
     * @return
     */
    @POST("{shopId}/order/{orderId}/assigntocourier")
    Observable<BaseEntity<JsonObject>> doAssignOrder(@Path("shopId") int shopId,@Path("orderId") long orderId,@Query("courierId") long courierId,
                                         @Query("token") String token);

    /**
     * 订单从小哥手中撤回
     * @param shopId
     * @param orderId
     * @param token
     * @return
     */
    @GET("{shopId}/order/{orderId}/recall")
    Observable<BaseEntity<JsonObject>> doRecallOrder(@Path("shopId") int shopId,@Path("orderId") long orderId,@Query("token") String token);


    /**
     * 获取门店基本信息
     * @param shopId
     * @param token
     * @return
     */
    @Headers("Cache-Control:public,max-age=1,max-stale=2")
    @GET("{shopId}/shop/info")
    Observable<BaseEntity<ShopInfo>> loadShopInfo(@Path("shopId") int shopId,@Query("token") String token);

    /**
     * 修改门店电话号码
     * @param shopId
     * @param shopTelephone
     * @param token
     * @return
     */
    @POST("{shopId}/shop/update")
    Observable<BaseEntity<JsonObject>> modifyShopTelephone(@Path("shopId") int shopId,@Query("shopTelephone") String shopTelephone,@Query("token") String token);
}
