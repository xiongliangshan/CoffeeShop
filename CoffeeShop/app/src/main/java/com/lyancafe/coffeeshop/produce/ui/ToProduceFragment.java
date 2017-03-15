package com.lyancafe.coffeeshop.produce.ui;


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
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenter;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToProduceFragment extends BaseFragment implements MainProduceFragment.FilterOrdersListenter,ToProduceView {


    @BindView(R.id.rv_to_produce) RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private ToProduceRvAdapter mAdapter;
    private Context mContext;
    public List<OrderBean> allOrderList = new ArrayList<>();
    private Handler mHandler;
    private ToProduceTaskRunnable mRunnable;

    private ToProducePresenter mToProducePresenter;
    public ToProduceFragment() {

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
        mToProducePresenter = new ToProducePresenterImpl(this.getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_to_produce, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }


    private void initViews() {
        mAdapter = new ToProduceRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToProducePresenter.loadToProduceOrderList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void filter(String category) {
        Log.d("xls","ToProduce category = "+category);
        mAdapter.setData(allOrderList, MainProduceFragment.category);
    }

    @Override
    public void showStartProduceConfirmDialog(final OrderBean orderBean) {
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(getActivity(), R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                //请求服务器改变该订单状态，由 待生产--生产中
                mToProducePresenter.reqStartProduceAndPrint(getActivity(),orderBean);
                //打印全部
                PrintHelper.getInstance().printOrderInfo(orderBean);
                PrintHelper.getInstance().printOrderItems(orderBean);
            }
        });
        grabConfirmDialog.setContent("订单 " + orderBean.getOrderSn() + " 开始生产？");
        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
        grabConfirmDialog.show();
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
    public void addOrdersToList(List<OrderBean> orders) {
        allOrderList.clear();
        allOrderList.addAll(orders);
        if(isVisible){
            mAdapter.setData(orders, MainProduceFragment.category);
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls","ToproduceFragment is Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ToProduceTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls","ToproduceFragment is InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    //新订单消息触发事件
    @Subscribe
    public void onNewOrderComing(NewOderComingEvent event){
        if(isResumed()){
           mToProducePresenter.loadToProduceOrderList();
        }

    }

    /**
     * 点击开始生产按钮事件
     * @param event
     */
    @Subscribe
    public void onStartProduceEvent(StartProduceEvent event){
        showStartProduceConfirmDialog(event.order);
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


    @Override
    public void removeItemFromList(int id) {
        mAdapter.removeOrderFromList(id);
    }

    class ToProduceTaskRunnable implements Runnable{
        @Override
        public void run() {
            mToProducePresenter.loadToProduceOrderList();
        }
    }
}
