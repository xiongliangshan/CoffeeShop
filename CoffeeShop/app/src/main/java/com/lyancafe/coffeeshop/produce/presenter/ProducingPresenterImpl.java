package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.http.BaseObserver;
import com.lyancafe.coffeeshop.produce.model.ProducingModel;
import com.lyancafe.coffeeshop.produce.model.ProducingModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingPresenterImpl implements ProducingPresenter{

    private ProducingView mProducingView;
    private ProducingModel mProducingModel;
    private Context mContext;

    public ProducingPresenterImpl(ProducingView mProducingView, Context mContext) {
        this.mProducingView = mProducingView;
        this.mContext = mContext;
        mProducingModel = new ProducingModelImpl();
    }


    @Override
    public void loadProducingOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.loadProducingOrders(user.getShopId(), user.getToken(), new BaseObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> producingList = orderBeanList;
                EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(1,producingList.size()));
                mProducingView.bindDataToView(producingList);
                OrderUtils.with().insertOrderList(producingList);
            }
        });
    }



    @Override
    public void doFinishProduced(final long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.dodoFinishProduced(user.getShopId(), orderId, user.getToken(), new BaseObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mProducingView.showToast(mContext.getString(R.string.do_success));
                int id  = jsonObject.get("id").getAsInt();
                mProducingView.removeItemFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));
                OrderUtils.with().updateOrder(orderId,4010);
            }
        });
    }
}