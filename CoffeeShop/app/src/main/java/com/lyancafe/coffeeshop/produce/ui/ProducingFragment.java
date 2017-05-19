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
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.produce.presenter.ProducingPresenter;
import com.lyancafe.coffeeshop.produce.presenter.ProducingPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ProducingView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

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
public class ProducingFragment extends BaseFragment implements MainProduceFragment.FilterOrdersListenter,ProducingView {

    private ProducingPresenter mProducingPresenter;

    @BindView(R.id.rv_producing) RecyclerView mRecyclerView;
    private Unbinder unbinder;
    private ProducingRvAdapter mAdapter;
    private Context mContext;

    public List<OrderBean> allOrderList = new ArrayList<>();

    private Handler mHandler;
    private ProducingTaskRunnable mRunnable;
    private LoadingDialog mLoadingDlg;

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
        mProducingPresenter = new ProducingPresenterImpl(this,this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_producing, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
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
    public void bindDataToListView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list, MainProduceFragment.category);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Override
    public void showLoading() {
        if(mLoadingDlg==null){
            mLoadingDlg = new LoadingDialog(getContext());
        }
        if(!mLoadingDlg.isShowing()){
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if(mLoadingDlg!=null && mLoadingDlg.isShowing()){
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void removeItemFromList(int id) {
        mAdapter.removeOrderFromList(id);
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
        dismissLoading();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls","producingFragment is Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ProducingTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
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
        showFinishProduceConfirmDialog(event.order);
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


    @Override
    public void filter(String category) {
        Log.d("xls","Producing category = "+category);
        mAdapter.setData(allOrderList, MainProduceFragment.category);
    }


    class ProducingTaskRunnable implements Runnable{
        @Override
        public void run() {
//            mProducingPresenter.loadProducingOrderList();
            mProducingPresenter.loadProducingOrders();
        }
    }
}
