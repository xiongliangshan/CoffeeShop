package com.lyancafe.coffeeshop.base;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface  BaseView <T> {

    //绑定数据到列表视图
    void bindDataToListView(List<T> list);

    //弹出Toast提示
    void showToast(String promptStr);
}
