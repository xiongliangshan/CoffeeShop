package com.lyancafe.coffeeshop.login.model;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.common.HttpHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

import static com.lyancafe.coffeeshop.common.LoginHelper.getLoginTime;
import static com.lyancafe.coffeeshop.common.LoginHelper.saveCurrentDayFirstLoginTime;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginModelImpl implements LoginModel{

    private static final String TAG = "login";
    @Override
    public void login(Activity activity, String userName, String password, final OnHandleLoginListener listener) {

        HttpHelper.getInstance().reqLogin(userName, password, new DialogCallback<XlsResponse>(activity) {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onLoginSuccess(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onLoginFailure(call,response,e);
            }
        });

    }


    @Override
    public void uploadDeviceInfo(String regId,final OnHandleUpLoadDeviceInfoListener listener) {
        HttpHelper.getInstance().reqUploadDeviceInfo(regId, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                listener.onUpLoadDeviceInfoSuccess(xlsResponse, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                listener.onUpLoadDeviceInfoFailure(call,response,e);
            }
        });
    }

    @Override
    public boolean isCurrentDayFirstLogin(Context context) {
        long currentTime = System.currentTimeMillis();
        long firstTime = getLoginTime(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time1 = sdf.format(new Date(firstTime));
        Log.d(TAG,"time 1 ="+time1);
        String time2 = sdf.format(new Date(currentTime));
        if(time1.equals(time2)){
            Log.d(TAG,"not current day first login");
            return false;
        }else{
            saveCurrentDayFirstLoginTime(context,currentTime);
            Log.d(TAG, "is first login");
            return true;
        }
    }

    public interface OnHandleLoginListener{
        void onLoginSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoginFailure(Call call, Response response, Exception e);
    }
    public interface OnHandleUpLoadDeviceInfoListener{
        void onUpLoadDeviceInfoSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onUpLoadDeviceInfoFailure(Call call, Response response, Exception e);
    }

}