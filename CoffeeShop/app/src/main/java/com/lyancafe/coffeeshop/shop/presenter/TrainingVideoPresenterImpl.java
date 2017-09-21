package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.shop.model.TrainingVideoModel;
import com.lyancafe.coffeeshop.shop.model.TrainingVideoModelImpl;
import com.lyancafe.coffeeshop.shop.view.TrainingVideoView;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class TrainingVideoPresenterImpl implements TrainingVideoPresenter {

    private Context context;
    private TrainingVideoModel trainingVideoModel;
    private TrainingVideoView trainingVideoView;

    public TrainingVideoPresenterImpl(Context context, TrainingVideoView trainingVideoView) {
        this.context = context;
        this.trainingVideoView = trainingVideoView;
        trainingVideoModel = new TrainingVideoModelImpl();
    }

    @Override
    public void loadVideos() {
        UserBean user = LoginHelper.getUser(context);
        trainingVideoModel.loadVideos(user.getShopId(), user.getToken(), new CustomObserver<List<VideoBean>>(context) {
            @Override
            protected void onHandleSuccess(List<VideoBean> videoBeen) {
                LogUtil.d("xls","loadVideos success :"+videoBeen.size());
                for(VideoBean videoBean:videoBeen){
                    LogUtil.d("xls",videoBean.getTitle()+" | "+videoBean.getUrl());
                }
                trainingVideoView.bindDataToView(videoBeen);
            }
        });
    }
}
