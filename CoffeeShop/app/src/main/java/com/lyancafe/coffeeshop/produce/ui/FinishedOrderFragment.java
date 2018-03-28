package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.produce.presenter.FinishedPresenter;
import com.lyancafe.coffeeshop.produce.presenter.FinishedPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.FinishedView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.utils.VSpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.DetailView;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedOrderFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener,
        FinishedView<OrderBean>, FinishedRvAdapter.FinishedCallback, DetailView.ActionCallback {

    @BindView(R.id.plmgv_order_list)
    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.detail_view)
    DetailView detailView;
    @BindView(R.id.btn_summarize)
    Button btnSummarize;
    @BindView(R.id.searchLayout)
    ConstraintLayout searchLayout;
    private FinishedRvAdapter mAdapter;
    private FinishedSummarizeAdapter mSummarizeAdapter;
    private long mLastOrderId = 0;
    private Context mContext;

    @BindView(R.id.tv_date)
    TextView dateText;
    @BindView(R.id.tv_order_count)
    TextView orderCountText;
    @BindView(R.id.tv_cup_count)
    TextView cupCountText;

    private Unbinder unbinder;

    private Handler mHandler;
    private FineshedTaskRunnable mRunnable;
    private FinishedPresenter mFinishedPresenter;

    private List<OrderBean> serverOrders = new ArrayList<>();
    private List<OrderBean> cacheOrders = new ArrayList<>();

    //当前订单模式
    private OrderMode currentMode = OrderMode.NORMAL;

    private SpaceItemDecoration spaceItemDecoration;
    private VSpaceItemDecoration vSpaceItemDecoration;

    public FinishedOrderFragment() {

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
        mFinishedPresenter = new FinishedPresenterImpl(getContext(), this);

        spaceItemDecoration = new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getContext()), false);
        vSpaceItemDecoration = new VSpaceItemDecoration(OrderHelper.dip2Px(12, getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_finished_order, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }


    private void initViews() {
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(spaceItemDecoration);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(true);

        mAdapter = new FinishedRvAdapter(getActivity());
        mAdapter.setCallback(this);
        pullLoadMoreRecyclerView.setAdapter(mAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateText.setText(sdf.format(new Date()));


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
    public void showEmpty(boolean isNeedToShow) {

        if (tvEmpty != null) {
            tvEmpty.setVisibility(isNeedToShow ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void bindDataToView(List<OrderBean> list) {
        serverOrders.clear();
        if (list != null && list.size() > 0) {
            showEmpty(false);
            serverOrders.addAll(list);
            mAdapter.setData(list);
            if (currentMode == OrderMode.SUMMARIZE){
                List<OrderBean> cacheList = OrderUtils.with().queryFinishedOrders();
                cacheOrders.clear();
                cacheOrders.addAll(cacheList);
                List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(cacheOrders);
                mSummarizeAdapter.setData(OrderHelper.caculateGroupList(groups));
            }
        } else {
            showEmpty(true);
        }

    }

    @Override
    public void updateDetail(OrderBean order) {
        if (detailView != null) {
            detailView.updateData(order);
        }
    }

    @Override
    public void appendListData(List<OrderBean> list) {
        serverOrders.addAll(list);
        mAdapter.setData(serverOrders);
    }

    @Override
    public void bindAmountDataToView(int ordersAmount, int cupsAmount) {
        if (orderCountText != null && cupCountText != null) {
            orderCountText.setText(String.valueOf(ordersAmount));
            cupCountText.setText(String.valueOf(cupsAmount));
        }
    }


    @Override
    public void reportIssue(long orderId) {
        ReportIssueDialog rid = ReportIssueDialog.newInstance(orderId);
        rid.show(getChildFragmentManager(), "report_issue");
    }

    @Override
    public void saveLastOrderId() {
        if (mAdapter.list.size() > 0) {
            Collections.sort(mAdapter.list, new Comparator<OrderBean>() {
                @Override
                public int compare(OrderBean o1, OrderBean o2) {
                    return (int) (o1.getId() - o2.getId());
                }
            });
            mLastOrderId = mAdapter.list.get(mAdapter.list.size() - 1).getId();
        } else {
            mLastOrderId = 0;
        }
    }

    @Override
    public void stopLoadingProgress() {
        if (pullLoadMoreRecyclerView != null) {
            pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void onLoadMore() {
        mFinishedPresenter.loadFinishedOrders(mLastOrderId, true);
    }


    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls", "FinishedOrderFragment  Visible");
        if (!isResumed()) {
            return;
        }
        mRunnable = new FineshedTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }

    @OnClick({R.id.btn_search, R.id.btn_summarize})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                search();
                break;
            case R.id.btn_summarize:
                //汇总模式
                if ("汇总模式".equals(btnSummarize.getText())) {
                    List<OrderBean> list = OrderUtils.with().queryFinishedOrders();
                    cacheOrders.clear();
                    cacheOrders.addAll(list);
                    if (cacheOrders.size() < 1) {
                        ToastUtil.show(getContext(), "没有可以汇总的订单!");
                        return;
                    }
                    btnSummarize.setText("详单模式");
                    switchMode(OrderMode.SUMMARIZE);
                } else if ("详单模式".equals(btnSummarize.getText())) {
                    btnSummarize.setText("汇总模式");
                    switchMode(OrderMode.NORMAL);
                }
                break;
        }

    }

    private void switchMode(OrderMode mode) {
        if (mode == OrderMode.SUMMARIZE) {
            //汇总模式
            detailView.setVisibility(View.GONE);
            long start = System.currentTimeMillis();
            List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(cacheOrders);
            List<SummarizeGroup> resultGroups = OrderHelper.caculateGroupList(groups);
            long end = System.currentTimeMillis();
            LogUtil.d("xiong", "计算数据所用时间:" + (end - start));

            renderSummarizeUI(resultGroups);
            Logger.getLogger().log("切换到 汇总模式");
        } else {
            //详单模式
            detailView.setVisibility(View.VISIBLE);
            renderNormalUI();
            Logger.getLogger().log("切换到 详单模式");
        }

        this.currentMode = mode;
    }


    // 执行搜索
    private void search() {
        String searchKey = etSearchKey.getText().toString();
        Logger.getLogger().log("已完成搜索 key = " + searchKey);
        if (TextUtils.isEmpty(searchKey)) {
            mAdapter.setSearchData(mAdapter.tempList);
            return;
        }
        try {
            if ("2333".equals(searchKey)) {
                LogUtil.v("FinishedOrderFragment", "has clear db Orders");
                Logger.getLogger().log("has clear db Orders");
                OrderUtils.with().clearTable();
            }
            mAdapter.searchOrder(Integer.parseInt(searchKey));
        } catch (NumberFormatException e) {
            showToast("数据太大或者类型不对");
            return;
        }

        MyUtil.hideKeyboard(etSearchKey);
        etSearchKey.setText("");
    }

    class FineshedTaskRunnable implements Runnable {
        @Override
        public void run() {
            mFinishedPresenter.loadOrderAmount();
            mFinishedPresenter.loadFinishedOrders(0, false);
        }
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls", "FinishedOrderFragment  InVisible");
        if (mHandler != null) {
            Log.d("xls", "removeCallbacks finished");
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * 渲染普通详单模式的UI
     */
    private void renderNormalUI() {
        searchLayout.setVisibility(View.VISIBLE);
        if (mAdapter == null) {
            mAdapter = new FinishedRvAdapter(getContext());
            mAdapter.setCallback(this);
        }

        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().removeItemDecoration(vSpaceItemDecoration);
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(spaceItemDecoration);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(true);
        pullLoadMoreRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 渲染汇总模式的UI
     *
     * @param groups
     */
    private void renderSummarizeUI(List<SummarizeGroup> groups) {
        searchLayout.setVisibility(View.INVISIBLE);
        if (mSummarizeAdapter == null) {
            mSummarizeAdapter = new FinishedSummarizeAdapter(getContext());
        }

        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().removeItemDecoration(spaceItemDecoration);
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(vSpaceItemDecoration);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(false);
        pullLoadMoreRecyclerView.setAdapter(mSummarizeAdapter);
        mSummarizeAdapter.setData(groups);

    }


}
