package com.lyancafe.coffeeshop.produce.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.http.BaseObserver;
import com.lyancafe.coffeeshop.produce.model.TomorrowModel;
import com.lyancafe.coffeeshop.produce.model.TomorrowModelImpl;
import com.lyancafe.coffeeshop.produce.view.TomorrowView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
        mTomorrowModel.loadTomorrowOrders(user.getShopId(), user.getToken(), new BaseObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> tomorrowList = orderBeanList;
                EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(4, tomorrowList.size()));
                mTomorrowView.bindDataToView(tomorrowList);
                OrderUtils.with().insertOrderList(tomorrowList);
            }
        });
    }
}
