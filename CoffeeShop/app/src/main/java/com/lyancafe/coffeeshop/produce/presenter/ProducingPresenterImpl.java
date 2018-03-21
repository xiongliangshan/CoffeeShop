package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.produce.model.ProducingModel;
import com.lyancafe.coffeeshop.produce.model.ProducingModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingPresenterImpl implements ProducingPresenter{
    public static final String TAG = "ProducingPresenterImpl";
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
        mProducingModel.loadProducingOrders(user.getShopId(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                EventBus.getDefault().post(new UpdateTabCount(TabList.TAB_PRODUCING, orderBeanList.size()));
                mProducingView.bindDataToView(orderBeanList);
                OrderUtils.with().insertOrderList(new CopyOnWriteArrayList<>(orderBeanList));
                //同步安卓本地和服务器的数据
                List<OrderBean> toProducedOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.PRODUCING);
                List<Long> idList = getRedundant(toProducedOrders,orderBeanList);
                if (idList.size() > 0) {
                    Log.v(TAG, "生产中的数据同步,orderId:" + idList);
                    Logger.getLogger().log("生产中的数据同步,orderId:" + idList);
                }
                for(Long id : idList){
                    OrderUtils.with().updateUnFindOrder(id);
                }
            }
        });
    }

    private List<Long> getRedundant(List<OrderBean> fromSQLite, List<OrderBean> fromInterface){
        List<Long> idList = new ArrayList<>();
        Map<Long, Long> idMap = new HashMap<>();
        for (OrderBean orderBean : fromInterface) {
            idMap.put(orderBean.getId(), orderBean.getId());
        }
        for(OrderBean orderBean : fromSQLite){
            if(!idMap.containsKey(orderBean.getId())){
                idList.add(orderBean.getId());
            }
        }
        return idList;
    }


    @Override
    public void doFinishProduced(final long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.dodoFinishProduced(user.getShopId(), orderId, new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mProducingView.showToast(mContext.getString(R.string.do_success));
                Logger.getLogger().log("完成生产订单 "+orderId+" 成功");
                int id  = jsonObject.get("id").getAsInt();
                mProducingView.removeItemFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));
                OrderUtils.with().updateOrder(orderId,4010);
            }
        });
    }

    @Override
    public void doCompleteBatchProduce(final List<Long> orderIds) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mProducingModel.doCompleteBatchProduce(user.getShopId(), orderIds, new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mProducingView.showToast(mContext.getString(R.string.do_success));
//                mProducingView.setMode(ListMode.NORMAL);
                JsonArray jsonArray = jsonObject.get("orderIds").getAsJsonArray();
                mProducingView.removeItemsFromList(orderIds);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,orderIds.size(),false));
                OrderUtils.with().updateBatchOrder(orderIds,4010);
            }
        });
    }
}