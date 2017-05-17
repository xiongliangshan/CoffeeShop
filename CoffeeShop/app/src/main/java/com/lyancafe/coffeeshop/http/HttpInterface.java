package com.lyancafe.coffeeshop.http;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.login.model.UserBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
     * @return
     */

    @POST("token")
    Observable<BaseEntity<UserBean>> login(@Body Map<String,Object> params);


    @POST("{shopId}/barista/{userId}/device")
    Observable<BaseEntity> uploadDeviceInfo(@Path("shopId") int shopId,@Path("userId") int userId,@QueryMap Map<String,Object> params);


}
