package com.lyancafe.coffeeshop.http;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.logger.Logger;

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
        builder.addInterceptor(customInterceptor);
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15,TimeUnit.SECONDS);
        builder.cache(new Cache(createDirectory(CSApplication.CACHE_DIR),1024*1024*20L));
        return new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    private static File createDirectory(String name){
        File directory = new File(name);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return directory;
    }


    /**
     * 拦截器
     */
    private final static Interceptor customInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            UserBean user = LoginHelper.getUser(CSApplication.getInstance());
            String token = user.getToken();
            if(token==null){
                token = "";
            }
            Request request = chain.request()
                    .newBuilder()
                    .header("token",token)
                    .build();


            Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();
            if(response.code()!=200){
                Logger.getLogger().error("接口请求失败:{method="+request.method()+", code="+response.code()+", url="+request.url()+"}");
            }

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
