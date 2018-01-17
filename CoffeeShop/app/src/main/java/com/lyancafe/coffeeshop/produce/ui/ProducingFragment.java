package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.produce.presenter.ProducingPresenter;
import com.lyancafe.coffeeshop.produce.presenter.ProducingPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProducingFragment extends BaseFragment implements ProducingView<OrderBean>,
        ProducingRvAdapter.ProducingCallback,DetailView.ActionCallback {

    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_finish_all)
    Button btnFinishAll;
    @BindView(R.id.detail_view)
    DetailView detailView;
    private ProducingPresenter mProducingPresenter;

    @BindView(R.id.rv_producing)
    RecyclerView mRecyclerView;
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
        mProducingPresenter = new ProducingPresenterImpl(this, this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_producing, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new ProducingRvAdapter(getActivity());
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
    }

    @Override
    public void bindDataToView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list);
    }


    @Override
    public void updateDetail(OrderBean order) {
        if(detailView!=null){
            detailView.updateData(order);
        }
    }


    @Override
    public void removeItemFromList(int id) {
        mAdapter.removeOrderFromList(id);
    }

    @Override
    public void removeItemsFromList(List<Long> ids) {
        mAdapter.removeOrdersFromList(ids);
    }

    @Override
    public void reportIssue(long orderId) {
        ReportIssueDialog rid = ReportIssueDialog.newInstance(orderId);
        rid.show(getChildFragmentManager(), "report_issue");
    }

    @Override
    public void showFinishProduceConfirmDialog(final OrderBean orderBean) {
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(getActivity(), R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                mProducingPresenter.doFinishProduced(orderBean.getId());
            }
        });
        grabConfirmDialog.setContent("订单 " + orderBean.getOrderSn() + " 生产完成？");
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
        if (mHandler != null) {
            mHandler = null;
        }
    }

    // 执行搜索
    private void search() {
        String searchKey = etSearchKey.getText().toString();
        Logger.getLogger().log("生产中搜索 key = " + searchKey);
        if (TextUtils.isEmpty(searchKey)) {
            mAdapter.setSearchData(mAdapter.getList());
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

    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls", "producingFragment is Visible");
        if (!isResumed()) {
            return;
        }
        mRunnable = new ProducingTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls", "producingFragment is InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * 点击生产完成的按钮事件
     *
     * @param event
     */
    @Subscribe
    public void onFinishProduceEvent(FinishProduceEvent event) {
//        showFinishProduceConfirmDialog(event.order);
        mProducingPresenter.doFinishProduced(event.order.getId());
    }


    //订单撤销事件
    @Subscribe
    public void onRevokeEvent(RevokeEvent event) {
        if (event.orderBean == null) {
            LogUtil.e("xls", "onRevokeEvent orderBean = null");
            return;
        }
        if (event.orderBean.getProduceStatus() == 4005) {
            removeItemFromList((int) event.orderBean.getId());
            EventBus.getDefault().postSticky(new ChangeTabCountByActionEvent(OrderAction.REVOKEORDER, 1, 1));
        }
    }


    //订单状态改变后刷新列表UI
    public void refreshListForStatus(long orderId, int status) {
        if (mAdapter == null) {
            return;
        }
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            OrderBean order = mAdapter.getList().get(i);
            if (orderId == order.getId()) {
                order.setStatus(status);
                mAdapter.notifyItemChanged(i);
                if (getParentFragment() instanceof MainProduceFragment) {
                    ((MainProduceFragment) getParentFragment()).updateOrderDetail(order);
                }
                break;
            }
        }
    }

    @OnClick({R.id.btn_search, R.id.btn_finish_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                search();
                break;
            case R.id.btn_finish_all:
                List<Long> orderIs = OrderHelper.getIdsFromOrders(mAdapter.getList());
                if (orderIs.size() == 0) {
                    showToast("没有可操作的订单");
                    return;
                }
                mProducingPresenter.doCompleteBatchProduce(orderIs);
                Logger.getLogger().log("一键全部完成 ，总数为: "+orderIs.size()+", 订单集合为:"+orderIs);
                break;
        }

    }


    class ProducingTaskRunnable implements Runnable {
        @Override
        public void run() {
            mProducingPresenter.loadProducingOrders();
        }
    }
}
