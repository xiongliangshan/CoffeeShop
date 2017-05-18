package com.lyancafe.coffeeshop.http;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.login.model.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
     * 加载物料列表内容
     * @param shopId
     * @param token
     * @return
     */
    @GET("{shopId}/supplies")
    Observable<BaseEntity<List<Material>>> loadMaterials(@Path("shopId") int shopId, @Query("token") String token);



}
