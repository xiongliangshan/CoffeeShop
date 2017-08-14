package com.lyancafe.coffeeshop.produce.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.produce.model.RevokedModel;
import com.lyancafe.coffeeshop.produce.model.RevokedModelImpl;
import com.lyancafe.coffeeshop.produce.view.RevokedView;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/11.
 */

public class RevokedPresenterImpl implements RevokedPresenter {

    private Context mContext;
    private RevokedView mRevokedView;
    private RevokedModel mRevokedModel;

    public RevokedPresenterImpl(Context mContext, RevokedView mRevokedView) {
        this.mContext = mContext;
        this.mRevokedView = mRevokedView;
        mRevokedModel = new RevokedModelImpl();
    }

    @Override
    public void loadRevokedOrders() {
        LogUtil.d("xls","loadRevokedOrders");
        Observable.create(new ObservableOnSubscribe<List<OrderBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<OrderBean>> e) throws Exception {
                e.onNext(mRevokedModel.loadRevokedOrders());
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<OrderBean>>() {
                @Override
                public void accept(@NonNull List<OrderBean> orderBeanList) throws Exception {
                    LogUtil.d("xls","accept");
                    EventBus.getDefault().post(new UpdateTabCount(4, orderBeanList==null?0:orderBeanList.size()));
                    mRevokedView.bindDataToView(orderBeanList);
                    for(OrderBean order:orderBeanList){
                        LogUtil.d("xls","order = "+order.toString());
                    }
                }
            });


    }
}
