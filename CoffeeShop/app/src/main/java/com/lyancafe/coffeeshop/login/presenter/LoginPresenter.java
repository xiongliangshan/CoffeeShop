package com.lyancafe.coffeeshop.login.presenter;

import android.app.Activity;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/13.
 */

public interface LoginPresenter {

    //检查登录状态
    void checkLoginStatus();

    //登录
    void login(Activity activity);

    //处理登录接口返回数据
    void handleLoginResponse(XlsResponse xlsResponse, Call call, Response response);

    //处理上传设备返回数据
    void handleUploadDeviceInfoResponse(XlsResponse xlsResponse,Call call, Response response);
}
