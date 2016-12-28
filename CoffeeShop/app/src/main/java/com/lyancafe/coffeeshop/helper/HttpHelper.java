package com.lyancafe.coffeeshop.helper;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/28.
 */
public class HttpHelper {
    private static HttpHelper http;

    public static HttpHelper getInstance(){
        if(http==null){
            return new HttpHelper();
        }
        return http;
    }




    public void reqToProduceData(boolean isSFMode){
        String token = LoginHelper.getToken(CSApplication.getInstance());
        int shopId = LoginHelper.getShopId(CSApplication.getInstance());
        String url = null;
        if(isSFMode){
            url = Urls.BASE_URL+shopId+"/orders/today/toproduce/tailwind?token="+token;
        }else{
            url = Urls.BASE_URL+shopId+"/orders/today/toproduce?token="+token;
        }
        OkGo.get(url)     // 请求方式和请求url
                .tag(this)                       // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // s 即为所需要的结果
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }
}
