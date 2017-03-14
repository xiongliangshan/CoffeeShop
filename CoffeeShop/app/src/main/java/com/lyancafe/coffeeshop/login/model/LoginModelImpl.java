package com.lyancafe.coffeeshop.login.model;


import android.app.Activity;

import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginModelImpl implements LoginModel{

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

    public interface OnHandleLoginListener{
        void onLoginSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onLoginFailure(Call call, Response response, Exception e);
    }
    public interface OnHandleUpLoadDeviceInfoListener{
        void onUpLoadDeviceInfoSuccess(XlsResponse xlsResponse, Call call, Response response);
        void onUpLoadDeviceInfoFailure(Call call, Response response, Exception e);
    }

}