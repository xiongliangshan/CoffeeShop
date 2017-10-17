package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.http.CustomObserver;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public interface TrainingVideoModel {

    //加载视频列表
    void loadVideos(int shopId, String token, CustomObserver<List<VideoBean>> observer);
}
