package com.lyancafe.coffeeshop.produce.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.login.ui.LoginActivity;
import com.lyancafe.coffeeshop.produce.model.TomorrowModel;
import com.lyancafe.coffeeshop.produce.model.TomorrowModelImpl;
import com.lyancafe.coffeeshop.produce.view.TomorrowView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/7/28.
 */

public class TomorrowPresenterImpl implements TomorrowPresenter {

    private Context mContext;
    private TomorrowModel mTomorrowModel;
    private TomorrowView mTomorrowView;

    public TomorrowPresenterImpl(Context context, TomorrowView mTomorrowView) {
        this.mContext = context;
        this.mTomorrowView = mTomorrowView;
        this.mTomorrowModel = new TomorrowModelImpl();
    }

    @Override
    public void loadTomorrowOrders() {
        UserBean user = LoginHelper.getUser(mContext);
        mTomorrowModel.loadTomorrowOrders(user.getShopId(), user.getToken(), new Observer<BaseEntity<List<OrderBean>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity<List<OrderBean>> listBaseEntity) {
                if(listBaseEntity.getStatus()==0){
                    List<OrderBean> tomorrowList = listBaseEntity.getData();
                    EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(4, tomorrowList.size()));
                    mTomorrowView.bindDataToView(tomorrowList);
                    OrderUtils.with().insertOrderList(tomorrowList);
                }else if(listBaseEntity.getStatus()==103){
                    mTomorrowView.showToast(listBaseEntity.getMessage());
                    UserBean userBean = LoginHelper.getUser(mContext);
                    userBean.setToken("");
                    LoginHelper.saveUser(mContext, userBean);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    if(mContext!=null && mContext instanceof Activity){
                        ((Activity) mContext).finish();
                    }
                }else{
                    mTomorrowView.showToast(listBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mTomorrowView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
