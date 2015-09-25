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
                final Jresp resp = ConnectionParams.doRequest(httpEntity);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(qry!=null){
                            qry.showResult(resp);
                        }
                    }
                });

            }
        };
    }

    public static void request(final HttpEntity httpEntity,final Context context,final Qry qry){
        executorForResource.execute(queryDataFromServer(httpEntity,context,qry));
    }

}
