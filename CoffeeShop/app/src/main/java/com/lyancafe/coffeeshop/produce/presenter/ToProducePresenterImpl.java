package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.printer.PrintFace;
import com.lyancafe.coffeeshop.produce.model.ToProduceModel;
import com.lyancafe.coffeeshop.produce.model.ToProduceModelImpl;
import com.lyancafe.coffeeshop.produce.ui.ListMode;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProducePresenterImpl implements ToProducePresenter{

    private Context mContext;
    private ToProduceModel mToProduceModel;
    private ToProduceView mToProduceView;

    public ToProducePresenterImpl(Context mContext, ToProduceView mToProduceView) {
        this.mContext = mContext;
        this.mToProduceView = mToProduceView;
        mToProduceModel = new ToProduceModelImpl();
    }



    @Override
    public void loadToProduceOrders() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.loadToProduceOrders(user.getShopId(), user.getToken(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                List<OrderBean> toProduceList = orderBeanList;
                EventBus.getDefault().post(new UpdateTabCount(TabList.TAB_TOPRODUCE, toProduceList.size()));
                mToProduceView.bindDataToView(toProduceList);
                OrderUtils.with().insertOrderList(toProduceList);
            }
        });
    }


    @Override
    public void doStartProduce(final long orderId, final boolean isScanCode) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.doStartProduce(user.getShopId(), orderId, user.getToken(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mToProduceView.showToast(mContext.getString(R.string.do_success));
                int id  = jsonObject.get("id").getAsInt();
                mToProduceView.removeItemFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1,isScanCode));
                OrderUtils.with().updateOrder(orderId,4005);

            }
        });
    }


    @Override
    public void doStartBatchProduce(final List<OrderBean> orders) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        final List<Long> orderIds = OrderHelper.getIdsFromOrders(orders);
        mToProduceModel.doStartBatchProduce(user.getShopId(), orderIds, user.getToken(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mToProduceView.showToast(mContext.getString(R.string.do_success));
                LogUtil.d("xls"," 批量生产 onHandleSuccess 开始打印");
                PrintFace.getInst().printBatch(orders);
                mToProduceView.setMode(ListMode.NORMAL);
                JsonArray jsonArray = jsonObject.get("orderIds").getAsJsonArray();
                mToProduceView.removeItemsFromList(orderIds);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,orderIds.size(),false));
                OrderUtils.with().updateBatchOrder(orderIds,4005);
            }
        });
    }

    @Override
    public void doNoPruduce(final long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.doNoProduce(user.getShopId(), orderId, user.getToken(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mToProduceView.showToast(mContext.getString(R.string.do_success));
                int id  = jsonObject.get("id").getAsInt();
                mToProduceView.removeItemFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.NONEEDPRODUCE,1,false));
                OrderUtils.with().updateOrder(orderId,4010);
            }
        });
    }
}