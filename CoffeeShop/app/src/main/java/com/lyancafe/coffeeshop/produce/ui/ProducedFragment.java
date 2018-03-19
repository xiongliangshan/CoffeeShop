package com.lyancafe.coffeeshop.produce.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.produce.presenter.ProducedPresenter;
import com.lyancafe.coffeeshop.produce.presenter.ProducedPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ProducedView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.DetailView;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ProducedFragment extends BaseFragment implements ProducedView<OrderBean>,
        ProducedRvAdapter.ProducedCallback, DetailView.ActionCallback {

    public List<OrderBean> allOrderList = new ArrayList<>();
    private List<OrderBean> notFetchList = new ArrayList<>();

    @BindView(R.id.rv_to_fetch)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.detail_view)
    DetailView detailView;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_not_fetch)
    RadioButton rbNotFetch;
    @BindView(R.id.rg_category)
    RadioGroup rgCategory;
    private ProducedRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private ToFetchTaskRunnable mRunnable;

    private ProducedPresenter mProducedPresenter;

    private boolean isOnlyNoFetch = false;

    public ProducedFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mProducedPresenter = new ProducedPresenterImpl(getContext(), this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_produced, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new ProducedRvAdapter(getActivity());
        mAdapter.setCallback(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);

        detailView.setCallback(this);

        etSearchKey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    search();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("xls", "ProducedFragment-onResume");
    }


    @Override
    public void bindDataToView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        updateNotFetchList(list);
        if(isOnlyNoFetch){
            mAdapter.setData(notFetchList);
        }else {
            mAdapter.setData(list);
        }

    }

    @Override
    public void updateDetail(OrderBean order) {
        if(detailView!=null){
            detailView.updateData(order);
        }

    }

    @Override
    public void reportIssue(long orderId) {
        ReportIssueDialog rid = ReportIssueDialog.newInstance(orderId);
        rid.show(getChildFragmentManager(), "report_issue");
    }

    // 执行搜索
    private void search() {
        String searchKey = etSearchKey.getText().toString();
        Logger.getLogger().log("已生产搜索 key = " + searchKey);
        if (TextUtils.isEmpty(searchKey)) {
            mAdapter.setSearchData(mAdapter.tempList);
            return;
        }
        try {
            mAdapter.searchOrder(Integer.parseInt(searchKey));
        } catch (NumberFormatException e) {
            showToast("数据太大或者类型不对");
            return;
        }

        MyUtil.hideKeyboard(etSearchKey);
        etSearchKey.setText("");
    }

    //订单撤销事件
    @Subscribe
    public void onRevokeEvent(RevokeEvent event) {
        if (event.orderBean == null) {
            LogUtil.e("xls", "onRevokeEvent orderBean = null");
            return;
        }
        if (removeItemFromList((int) event.orderBean.getId())) {
            EventBus.getDefault().postSticky(new ChangeTabCountByActionEvent(OrderAction.REVOKEORDER, 2, 1));
        }
    }

    @Override
    public boolean removeItemFromList(int id) {
        List<OrderBean> list = new ArrayList<>();
        list.addAll(allOrderList);
        for(int i=list.size()-1;i>=0;i--){
            if(list.get(i).getId()==id){
                list.remove(i);
                bindDataToView(list);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls", "ProducedFragment-onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("xls", "ProducedFragment-onDetach");
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onVisible() {
        Log.d("xls", "ProducedFragment Visible");
        if (!isResumed()) {
            return;
        }
        mRunnable = new ToFetchTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);
    }

    @Override
    public void onInVisible() {
        Log.d("xls", "ProducedFragment InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }


    @OnClick({R.id.btn_search,R.id.rb_not_fetch, R.id.rb_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                search();
                break;
            case R.id.rb_not_fetch:
                //未取货
                isOnlyNoFetch = true;
                mAdapter.setData(notFetchList);
                break;
            case R.id.rb_all:
                //全部
                isOnlyNoFetch = false;
                mAdapter.setData(allOrderList);
                break;
        }
    }


    class ToFetchTaskRunnable implements Runnable {
        @Override
        public void run() {
            mProducedPresenter.loadToFetchOrders();
        }
    }


    /**
     * 更新已经有集合
     * @param allOrders
     */
    private void updateNotFetchList(List<OrderBean> allOrders){
        notFetchList.clear();
        if(allOrders==null || allOrders.size()==0){
            return;
        }
        for(OrderBean orderBean:allOrders){
            if(orderBean.getStatus()<=3020){
                notFetchList.add(orderBean);
            }
        }
    }
}
