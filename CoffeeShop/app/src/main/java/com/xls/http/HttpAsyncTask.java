package com.xls.http;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.LoginActivity;
import com.lyancafe.coffeeshop.dialog.ProgressHUD;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.net.ConnectException;
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


    private static Runnable queryDataFromServer(final HttpEntity httpEntity,final Context context,final Qry qry,final Dialog dialog){
        return new Runnable(){
            @Override
            public void run() {
                //判断是否有网
                if (!HttpUtils.isOnline(context)) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    HttpUtils.showToastAsync(context, "请检查网络连接");
                    return;
                }
                final Jresp resp = ConnectionParams.doRequest(httpEntity,context);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(qry!=null && resp!=null){
                            if(resp.status == MyUtil.STATUS_INVALID_TOKEN){
                                ToastUtil.showToast(context,resp.message);
                                LoginHelper.saveToken(context, "");
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }else {
                                qry.showResult(resp);
                            }
                        }else{
                            if(!TextUtils.isEmpty(ConnectionParams.exceptionInfo)){
                                HttpUtils.showToastAsync(context, ConnectionParams.exceptionInfo);
                            }
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog!=null){
                                    dialog.dismiss();
                                }
                            }
                        },100);
                    }
                });

            }
        };
    }

    public static void request(final HttpEntity httpEntity,final Context context,final Qry qry,boolean isShowProgress){
        if(isShowProgress){
            final ProgressHUD progressHUD = ProgressHUD.show(context, context.getResources().getString(R.string.loading), true,
                    false, null);
            executorForResource.execute(queryDataFromServer(httpEntity, context, qry, progressHUD));
        }else{
            executorForResource.execute(queryDataFromServer(httpEntity, context, qry, null));
        }


    }

}
