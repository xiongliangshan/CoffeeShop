package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.produce.model.ProducingModel;
import com.lyancafe.coffeeshop.produce.model.ProducingModelImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/15
*/

public class ProducingPresenterImpl implements ProducingPresenter,ProducingModelImpl.OnHandleProducingListener{

    private ProducingView mProducingView;
    private ProducingModel mProducingModel;
    private Context mContext;

    public ProducingPresenterImpl(ProducingView mProducingView, Context mContext) {
        this.mProducingView = mProducingView;
        this.mContext = mContext;
        mProducingModel = new ProducingModelImpl();
    }

    @Override
    public void loadProducingOrderList() {
        mProducingModel.loadProducingOrderList(this);
    }

    @Override
    public void handleProudcingResponse(XlsResponse xlsResponse, Call call, Response response) {
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(mContext, xlsResponse);
        EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(1,orderBeans.size()));
        mProducingView.addOrdersToList(orderBeans);
    }

    @Override
    public void reqFinishProduce(final Activity activity, OrderBean order) {
        HttpHelper.getInstance().reqFinishedProduce(order.getId(), new DialogCallback<XlsResponse>(activity) {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleFinishedProduceResponse(activity,xlsResponse,call,response);
            }
        });
    }

    @Override
    public void handleFinishedProduceResponse(Activity activity, XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status == 0){
            ToastUtil.showToast(activity, R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            mProducingView.removeItemFromList(id);
            EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));

        }else{
            ToastUtil.showToast(activity, xlsResponse.message);
        }
    }

    @Override
    public void onProducingSuccess(XlsResponse xlsResponse, Call call, Response response) {
        handleProudcingResponse(xlsResponse,call,response);
    }

    @Override
    public void onProducingFailure(Call call, Response response, Exception e) {

    }
}