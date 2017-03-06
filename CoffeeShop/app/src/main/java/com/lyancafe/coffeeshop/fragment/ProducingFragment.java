package com.lyancafe.coffeeshop.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.ProducingRvAdapter;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProducingFragment extends BaseFragment implements OrdersFragment.FilterOrdersListenter{

    @BindView(R.id.rv_producing) RecyclerView mRecyclerView;
    private Unbinder unbinder;
    private ProducingRvAdapter mAdapter;
    private Context mContext;

    public List<OrderBean> allOrderList = new ArrayList<>();

    private Handler mHandler;
    private ProducingTaskRunnable mRunnable;

    public ProducingFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_producing, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews(contentView);
        return contentView;
    }

    private void initViews(View contentView) {
        mAdapter = new ProducingRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        Log.d("xls","producingFragment is Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ProducingTaskRunnable();
        mHandler.postDelayed(mRunnable,1000);

    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        Log.d("xls","producingFragment is InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * 点击生产完成的按钮事件
     * @param event
     */
    @Subscribe
    public void onFinishProduceEvent(FinishProduceEvent event){
        finishProduce(mContext, event.order);
    }

    /**
     * 处理订单撤回状态刷新
     * @param event
     */
    @Subscribe
    public void onRecallOrderEvent(RecallOrderEvent event){
        if(event.tabIndex==1){
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

    //点击生产完成
    public void finishProduce(final Context context,final OrderBean order){
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                HttpHelper.getInstance().reqFinishedProduce(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleFinishedProduceResponse(xlsResponse,call,response);
                    }
                });
            }
        });
        grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
        grabConfirmDialog.show();
    }


    /**
     * 处理生产完成动作返回的结果
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleFinishedProduceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status == 0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            mAdapter.removeOrderFromList(id);
            EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));

        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }



    //处理服务器返回数据---生产中
    private void handleProudcingResponse(XlsResponse xlsResponse,Call call,Response response){
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
        EventBus.getDefault().post(new UpdateProduceFragmentTabOrderCount(1,orderBeans.size()));
        allOrderList.clear();
        allOrderList.addAll(orderBeans);
        mAdapter.setData(orderBeans, OrdersFragment.category);
    }


    @Override
    public void filter(String category) {
        Log.d("xls","Producing category = "+category);
        mAdapter.setData(allOrderList,OrdersFragment.category);
    }


    class ProducingTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqProducingData(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleProudcingResponse(xlsResponse,call,response);
                }

            });
        }
    }
}
