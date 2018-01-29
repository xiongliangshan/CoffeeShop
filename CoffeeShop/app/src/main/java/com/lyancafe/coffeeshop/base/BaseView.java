package com.lyancafe.coffeeshop.base;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface BaseView{

    //弹出Toast提示
    void showToast(String promptStr);

    //显示Loading进度框
    void showLoading();

    //关闭Loading进度框
    void dismissLoading();
}
