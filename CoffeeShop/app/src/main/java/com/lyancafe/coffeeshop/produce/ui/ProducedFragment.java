package com.lyancafe.coffeeshop.produce.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.produce.presenter.ProducedPresenter;
import com.lyancafe.coffeeshop.produce.presenter.ProducedPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ProducedView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ProducedFragment extends BaseFragment implements ProducedView{

    public List<OrderBean> allOrderList = new ArrayList<>();

    @BindView(R.id.rv_to_fetch) RecyclerView mRecyclerView;
    private ProducedRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private ToFetchTaskRunnable mRunnable;

    private ProducedPresenter mProducedPresenter;

    public ProducedFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mProducedPresenter = new ProducedPresenterImpl(getContext(),this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProducedPresenter.loadToFetchOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView =  inflater.inflate(R.layout.fragment_produced, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new ProducedRvAdapter(getActivity());
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
    public void bindDataToView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list, MainProduceFragment.category);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            mProducedPresenter.loadToFetchOrders();
        }
    }
}
