package com.lyancafe.coffeeshop.main.presenter;

import android.app.Activity;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface MainPresenter {

    //检测版本更新
    void checkUpdate(Activity activity, boolean isBackground);

    //处理检测更新接口返回数据
    void handleCheckUpdateResponse(XlsResponse xlsResponse, Call call, Response response);

    //退出登录
    void exitLogin();

    //处理退出登录接口返回
    void handleLoginOutResponse(XlsResponse xlsResponse, Call call, Response response);

    //退出登录，重置token
    void resetToken();
}
