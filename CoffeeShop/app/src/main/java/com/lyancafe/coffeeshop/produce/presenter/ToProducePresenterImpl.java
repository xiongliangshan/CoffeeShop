package com.lyancafe.coffeeshop.produce.presenter;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BatchOrder;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.LatelyCountEvent;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.printer.PrintFace;
import com.lyancafe.coffeeshop.produce.model.ToProduceModel;
import com.lyancafe.coffeeshop.produce.model.ToProduceModelImpl;
import com.lyancafe.coffeeshop.produce.ui.ListMode;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.SoundPoolUtil;
import com.tencent.tinker.loader.shareutil.ShareOatUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProducePresenterImpl implements ToProducePresenter{
    public static final String TAG = "ToProducePresenterImpl";
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
        mToProduceModel.loadToProduceOrders(user.getShopId(), new CustomObserver<List<OrderBean>>(mContext) {
            @Override
            protected void onHandleSuccess(List<OrderBean> orderBeanList) {
                EventBus.getDefault().post(new UpdateTabCount(TabList.TAB_TOPRODUCE, orderBeanList.size()));
                mToProduceView.bindDataToView(orderBeanList);
                OrderUtils.with().insertOrderList(new CopyOnWriteArrayList<>(orderBeanList));
                //同步安卓本地和服务器的数据
                List<OrderBean> toProducedOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.UNPRODUCED);
                List<Long> idList = getRedundant(toProducedOrders,orderBeanList);
                if (idList.size() > 0) {
                    Log.v(TAG, "生产中的数据同步,orderId:" + idList);
                    Logger.getLogger().log("生产中的数据同步,orderId:" + idList);
                }
                for(Long id : idList){
                    OrderUtils.with().updateUnFindOrder(id);
                }
                //如果待生产列表为空，则向服务器查询最近的单量和杯量
                if (orderBeanList.size() <= 0){
                    loadLatelyCount();
                }else {
                    EventBus.getDefault().post(new LatelyCountEvent("0", "0", "0"));
                }
            }
        });
    }

    private void loadLatelyCount(){
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.loadLatelyCount(user.getShopId(), new CustomObserver<JsonObject>(mContext) {

            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                String latelyMin = "0";
                String orderNum = "0";
                String orderCups = "0";
                if (jsonObject != null){
                    latelyMin = jsonObject.get("latelyMin")==null?"0":jsonObject.get("latelyMin").toString();
                    orderNum = jsonObject.get("orderNum")==null?"0":jsonObject.get("orderNum").toString();
                    orderCups = jsonObject.get("orderCups")==null?"0":jsonObject.get("orderCups").toString();
                }
                EventBus.getDefault().post(new LatelyCountEvent(latelyMin, orderNum, orderCups));
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
    public void doStartProduce(final OrderBean order, final boolean isAuto) {
        /*
            isAuto true 先请求服务器接口，返回成功后，报声音与打印
            isAuto false 先报声音和打印后，请求服务器接口
        */
        if(isAuto){
            Logger.getLogger().log("自动生产订单:{" + order.getId() + "，priority = " +order.getPriority() + "}");
        }else {
            Logger.getLogger().log("手动生产订单:{" + order.getId() +  "}");
            SoundPoolUtil.create(CSApplication.getInstance(), R.raw.start_produce);
            PrintFace.getInst().startPrintWholeOrderTask(order);
        }
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.doStartProduce(user.getShopId(), order.getId(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mToProduceView.showToast(mContext.getString(R.string.do_success));
                OrderUtils.with().updateOrder(order.getId(),4005);
                int id  = jsonObject.get("id").getAsInt();
                mToProduceView.removeItemFromList(id);
                UserBean user = LoginHelper.getUser(CSApplication.getInstance());
                if (user.isOpenFulfill()) {
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1,false));
                } else {
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1,order.getWxScan()));
                }
                if(isAuto){
                    PrintFace.getInst().startPrintWholeOrderTask(order);
                    SoundPoolUtil.create(CSApplication.getInstance(), R.raw.start_produce);
                }
            }

            @Override
            protected void onHandleFailed(String msg) {
                super.onHandleFailed(msg);
                Logger.getLogger().error("生产订单失败:"+msg);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Logger.getLogger().error("生产接口出错:"+e.getMessage());
            }
        });
    }


    @Override
    public void doStartBatchProduce(final List<OrderBean> orders) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        final List<Long> orderIds = new ArrayList<>();
        final List<Long> scanIds = new ArrayList<>();
        OrderHelper.getIdsFromOrders(orders,orderIds,scanIds);
        BatchOrder batchOrder = new BatchOrder(orderIds,scanIds);
        final List<Long> allOrderIds = new ArrayList<>();
        allOrderIds.addAll(orderIds);
        allOrderIds.addAll(scanIds);
        mToProduceModel.doStartBatchProduce(user.getShopId(),batchOrder,new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mToProduceView.showToast(mContext.getString(R.string.do_success));
                LogUtil.d("xls"," 批量生产 onHandleSuccess 开始打印");
                PrintFace.getInst().printBatch(orders);
                mToProduceView.setMode(ListMode.NORMAL);
                mToProduceView.removeItemsFromList(allOrderIds);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,orderIds.size(),false));

                OrderUtils.with().updateBatchOrder(orderIds,4005);
                if(scanIds.size()>0){
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,scanIds.size(),true));
                    OrderUtils.with().updateBatchOrder(scanIds,4010);
                    OrderUtils.with().updateStatusBatch(scanIds,6000);
                }

            }

            @Override
            protected void onHandleFailed(String msg) {
                super.onHandleFailed(msg);
                Logger.getLogger().error("批量生产失败:"+msg);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Logger.getLogger().error("批量生产接口出错:"+e.getMessage());
            }
        });
    }

    @Override
    public void doNoPruduce(final long orderId) {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        mToProduceModel.doNoProduce(user.getShopId(), orderId, new CustomObserver<JsonObject>(mContext,true) {
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