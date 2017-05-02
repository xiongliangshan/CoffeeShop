package com.lyancafe.coffeeshop.produce.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.NaiGaiEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.common.HttpHelper;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.login.ui.LoginActivity;
import com.lyancafe.coffeeshop.produce.model.ToProduceModel;
import com.lyancafe.coffeeshop.produce.model.ToProduceModelImpl;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/14
*/

public class ToProducePresenterImpl implements ToProducePresenter,ToProduceModelImpl.OnHandleToProduceListener{

    private Context mContext;
    private ToProduceModel mToProduceModel;
    private ToProduceView mToProduceView;

    public ToProducePresenterImpl(Context mContext, ToProduceView mToProduceView) {
        this.mContext = mContext;
        this.mToProduceView = mToProduceView;
        mToProduceModel = new ToProduceModelImpl();
    }

    @Override
    public void loadToProduceOrderList() {
        mToProduceModel.loadToProduceOrderList(this);
    }



    @Override
    public void onToProSuccess(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(mContext, xlsResponse);
            EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(0, orderBeans.size()));
            mToProduceView.bindDataToListView(orderBeans);
            EventBus.getDefault().post(new NaiGaiEvent(OrderHelper.caculateNaiGai(orderBeans)));

        }else if(xlsResponse.status==103){
            //token 无效
            mToProduceView.showToast(xlsResponse.message);
            UserBean userBean = LoginHelper.getLoginBean(mContext);
            userBean.setToken("");
            LoginHelper.saveLoginBean(mContext, userBean);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if(mContext!=null && mContext instanceof Activity){
                ((Activity) mContext).finish();
            }

        }
    }

    @Override
    public void onToProFailure(Call call, Response response, Exception e) {

    }

    @Override
    public void reqStartProduceAndPrint(final Activity activity, OrderBean order) {
        HttpHelper.getInstance().reqStartProduce(order.getId(), new DialogCallback<XlsResponse>(activity) {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                if(xlsResponse.status == 0){
                    mToProduceView.showToast(mContext.getString(R.string.do_success));
                    int id  = xlsResponse.data.getIntValue("id");
                    mToProduceView.removeItemFromList(id);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1));
                }else{
                    mToProduceView.showToast(xlsResponse.message);
                }
            }
        });
    }

}