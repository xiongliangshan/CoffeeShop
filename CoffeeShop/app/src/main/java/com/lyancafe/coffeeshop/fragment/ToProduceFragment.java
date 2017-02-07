package com.lyancafe.coffeeshop.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.LoginActivity;
import com.lyancafe.coffeeshop.adapter.ToProduceRvAdapter;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToProduceFragment extends BaseFragment {


    private RecyclerView mRecyclerView;
    private ToProduceRvAdapter mAdapter;
    private Context mContext;
    public ToProduceFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_to_produce, container, false);
        initViews(contentView);
        return contentView;
    }


    private void initViews(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_to_produce);
        mAdapter = new ToProduceRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(OrdersFragment.tabIndex==0){
            HttpHelper.getInstance().reqToProduceData(LoginHelper.getLimitLevel(mContext), new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleToProudceResponse(xlsResponse,call,response);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        HttpHelper.getInstance().reqToProduceData(LoginHelper.getLimitLevel(mContext), new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleToProudceResponse(xlsResponse,call,response);
            }
        });
    }


    //新订单消息触发事件
    @Subscribe
    public void onNewOrderComing(NewOderComingEvent event){
        if(isResumed()){
            HttpHelper.getInstance().reqToProduceData(LoginHelper.getLimitLevel(mContext), new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleToProudceResponse(xlsResponse,call,response);
                }
            });
        }

    }

    /**
     * 点击开始生产按钮事件
     * @param event
     */
    @Subscribe
    public void onStartProduceEvent(StartProduceEvent event){
        startProduceAndPrint(mContext, event.order);
    }

    /**
     * 处理订单撤回状态刷新
     * @param event
     */
    @Subscribe
    public void onRecallOrderEvent(RecallOrderEvent event){
        if(event.tabIndex==0){
            for(int i=0;i<mAdapter.list.size();i++) {
                OrderBean order = mAdapter.list.get(i);
                if (event.orderId == order.getId()) {
                    order.setStatus(OrderStatus.UNASSIGNED);
                    mAdapter.notifyItemChanged(i);
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(order));
                    break;
                }
            }
        }

    }

    //点击开始生产并打印
    public void startProduceAndPrint(final Context context,final OrderBean order){
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                //请求服务器改变该订单状态，由 待生产--生产中
                HttpHelper.getInstance().reqStartProduce(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleStartProduceResponse(xlsResponse,call,response);
                    }
                });
                //打印全部
                PrintHelper.getInstance().printOrderInfo(order);
                PrintHelper.getInstance().printOrderItems(order);
            }
        });
        grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
        grabConfirmDialog.show();

    }


    /**
     * 处理开始生产结果
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleStartProduceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status == 0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            mAdapter.removeOrderFromList(id);
            EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1));
        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }


    //处理服务器返回数据---待生产
    private void handleToProudceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
            EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(0, orderBeans.size()));
            mAdapter.setData(orderBeans);
        }else if(xlsResponse.status==103){
            //token 无效
            ToastUtil.showToast(mContext, xlsResponse.message);
            LoginBean loginBean = LoginHelper.getLoginBean(mContext);
            loginBean.setToken("");
            LoginHelper.saveLoginBean(mContext, loginBean);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            Activity activity = getActivity();
            if(activity!=null){
                activity.finish();
            }

        }

    }
}
