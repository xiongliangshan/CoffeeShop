package com.lyancafe.coffeeshop.delivery.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.delivery.presenter.ToFetchPresenter;
import com.lyancafe.coffeeshop.delivery.presenter.ToFetchPresenterImpl;
import com.lyancafe.coffeeshop.delivery.view.ToFetchView;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ToFetchFragment extends BaseFragment implements MainDeliverFragment.FilterOrdersListenter,ToFetchView{

    public List<OrderBean> allOrderList = new ArrayList<>();

    @BindView(R.id.rv_to_fetch) RecyclerView mRecyclerView;
    private ToFetchRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private ToFetchTaskRunnable mRunnable;

    private ToFetchPresenter mToFetchPresenter;

    public ToFetchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mToFetchPresenter = new ToFetchPresenterImpl(getContext(),this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView =  inflater.inflate(R.layout.fragment_to_fetch, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new ToFetchRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("xls","ToFetchFragment-onResume");
    }

    @Override
    public void bindDataToListView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list, MainDeliverFragment.category);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","ToFetchFragment-onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("xls","ToFetchFragment-onDetach");
    }

    @Override
    public void filter(String category) {
        mAdapter.setData(allOrderList, MainDeliverFragment.category);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 咖啡师从小哥手里撤回订单
     * @param event
     */
    @Subscribe
    public  void onRecallOrderEvent(RecallOrderEvent event){
        if(event.tabIndex==10){
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
    public void onVisible() {
        Log.d("xls","ToFetchFragment Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ToFetchTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);
    }

    @Override
    public void onInVisible() {
        Log.d("xls","ToFetchFragment InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }


    class ToFetchTaskRunnable implements Runnable{
        @Override
        public void run() {
           mToFetchPresenter.loadToFetchOrderList();
        }
    }
}
