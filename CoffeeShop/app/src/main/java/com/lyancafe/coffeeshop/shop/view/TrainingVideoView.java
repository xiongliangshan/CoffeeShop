package com.lyancafe.coffeeshop.shop.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.VideoBean;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public interface TrainingVideoView<T> extends BaseView {

    //绑定数据到列表视图
    void bindDataToView(List<T> list);

}
