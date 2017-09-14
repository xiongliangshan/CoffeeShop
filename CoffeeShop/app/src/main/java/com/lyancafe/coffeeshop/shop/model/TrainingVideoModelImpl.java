package com.lyancafe.coffeeshop.shop.model;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class TrainingVideoModelImpl implements TrainingVideoModel {

    @Override
    public void loadVideos(int shopId, String token, CustomObserver<List<VideoBean>> observer) {
        RetrofitHttp.getRetrofit().loadVideos(shopId,token)
                .compose(RxHelper.<BaseEntity<List<VideoBean>>>io_main())
                .subscribe(observer);
    }
}
