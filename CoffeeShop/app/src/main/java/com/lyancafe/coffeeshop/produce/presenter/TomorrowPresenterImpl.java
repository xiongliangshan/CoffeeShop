package com.lyancafe.coffeeshop.produce.presenter;

import android.content.Context;

import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.produce.model.TomorrowModel;
import com.lyancafe.coffeeshop.produce.model.TomorrowModelImpl;
import com.lyancafe.coffeeshop.produce.view.TomorrowView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        mTomorrowModel.loadTomorrowOrders(user.getShopId(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> tomorrowList = orderBeanList;
                EventBus.getDefault().post(new UpdateTabCount(TabList.TAB_TOMORROW, tomorrowList.size()));
                mTomorrowView.bindDataToView(tomorrowList);
                OrderUtils.with().insertOrderList(new CopyOnWriteArrayList<OrderBean>(tomorrowList));
            }
        });
    }
}
