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
    void checkUpdate(boolean isShowProgress);

    //退出登录
    void exitLogin();

}
