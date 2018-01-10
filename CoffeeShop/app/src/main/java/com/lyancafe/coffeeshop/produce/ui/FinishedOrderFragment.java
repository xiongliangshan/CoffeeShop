package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.produce.presenter.FinishedPresenter;
import com.lyancafe.coffeeshop.produce.presenter.FinishedPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.FinishedView;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.DetailView;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.text.SimpleDateFormat;
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
        FinishedView<OrderBean>,FinishedRvAdapter.FinishedCallback,DetailView.ActionCallback {

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
    private FinishedRvAdapter mAdapter;
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
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(12, mContext), false));
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
        if (list != null && list.size() > 0) {
            showEmpty(false);
            mAdapter.setData(list);
        } else {
            showEmpty(true);
        }

    }

    @Override
    public void updateDetail(OrderBean order) {
        if(detailView!=null){
            detailView.updateData(order);
        }
    }

    @Override
    public void appendListData(List<OrderBean> list) {
        mAdapter.addData(list);
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

    @OnClick(R.id.btn_search)
    public void onViewClicked() {
        search();
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


}
