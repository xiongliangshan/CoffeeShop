package com.xls.http;

import android.content.Context;
import android.os.Handler;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/8/31.
 */
public class HttpAsyncTask {
    private static final String TAG = "HttpAsyncTask";
    public static final Handler handler = new Handler();
    public static ThreadPoolExecutor executorForResource = new ThreadPoolExecutor(5, 15, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


    private static Runnable queryDataFromServer(final HttpEntity httpEntity,final Context context,final Qry qry){
        return new Runnable(){
            @Override
            public void run() {
                //判断是否有网
                if (!HttpUtils.isOnline(context)) {
                    qry.showResult(null);
                    HttpUtils.showToastAsync(context.getApplicationContext(),"请检查网络连接");
                    return;
                }
                if(HttpEntity.POST.equalsIgnoreCase(httpEntity.getMethod())){
                    // post请求
                    String jsonParams = Response.createPostURLParams(httpEntity.getParams());
                    final Jresp resp = ConnectionParams.post(httpEntity.getUrl(), jsonParams);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (resp == null) {
                                if (qry != null) {
                                    qry.showResult(null);
                                }
                                return;
                            }
                        }
                    });
                }else if(HttpEntity.GET.equalsIgnoreCase(httpEntity.getMethod())){
                    // get 请求

                    String wholeUrl = Response.createGetURLParams(httpEntity.getUrl(), httpEntity.getParams());
                    final Jresp resp = ConnectionParams.get(wholeUrl);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (resp == null) {
                                if (qry != null) {
                                    qry.showResult(null);
                                }
                                return;
                            }
                        }
                    });
                }


            }
        };
    }
    /**
     * 发送post请求
     */
    public static void requestByPost(String url,Map<String, Object> params,Context context,Qry qry){
        HttpEntity httpEntity = new HttpEntity(HttpEntity.POST,url,params);
        executorForResource.execute(queryDataFromServer(httpEntity,context,qry));
    }

    /**
     * 发送get请求
     */
    public static void requestByGet(String url,Map<String, Object> params,Context context,Qry qry){
        HttpEntity httpEntity = new HttpEntity(HttpEntity.GET,url,params);
        executorForResource.execute(queryDataFromServer(httpEntity,context,qry));
    }
}
