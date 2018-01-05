package com.lyancafe.coffeeshop.http;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
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
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);
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
            LogUtil.d("xiong","普通拦截器");
            UserBean user = LoginHelper.getUser(CSApplication.getInstance());
            String token = user.getToken();
            if(token==null){
                token = "";
            }
            if(chain.request().url().toString().contains("upload")){
                LogUtil.d("xiong","upload 不拦截");
                return chain.proceed(chain.request());
            }
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("content-type","application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("token",token)
                    .build();
            LogUtil.d("xiong",request.url().toString()  +" | token = "+token);
            return chain.proceed(request);
        }
    };

    /**
     * 缓存拦截器
     */
    private final static Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            LogUtil.d("xiong","缓存拦截器");
            Request request = chain.request();

            Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();
            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();

        }
    };

    public static void reset(){
        singleton = null;
    }

}
