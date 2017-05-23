package com.lyancafe.coffeeshop.http;

import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/3/30.
 */

public class RetrofitHttp {

    private static HttpInterface singleton;


    public static HttpInterface getRetrofit() {
        if (singleton == null) {
            synchronized (RetrofitHttp.class) {
                singleton = createRetrofit().create(HttpInterface.class);
            }
        }
        return singleton;
    }


    private static Retrofit createRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(commomInterceptor);
        builder.addInterceptor(cacheInterceptor);
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15,TimeUnit.SECONDS);
        builder.cache(new Cache(createDirectory(CSApplication.CACHE_DIR),1024*1024*20L));
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }


    private static File createDirectory(String name){
        File directory = new File(name);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return directory;
    }

    /**
     * 普通拦截器
     */
    private final static Interceptor commomInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            LogUtil.d("http","普通拦截器");
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("content-type","application/x-www-form-urlencoded; charset=UTF-8")
                    .build();
            return chain.proceed(request);
        }
    };

    /**
     * 缓存拦截器
     */
    private final static Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            LogUtil.d("http","缓存拦截器");
            Request request = chain.request();

            Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();
            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();

        }
    };

}
