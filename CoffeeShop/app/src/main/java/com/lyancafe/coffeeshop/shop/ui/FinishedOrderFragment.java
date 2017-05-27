package com.lyancafe.coffeeshop.shop.ui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.event.UpdateFinishedOrderDetailEvent;
import com.lyancafe.coffeeshop.shop.presenter.FinishedPresenter;
import com.lyancafe.coffeeshop.shop.presenter.FinishedPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.FinishedView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedOrderFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener, FinishedView {

    @BindView(R.id.plmgv_order_list)
    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    private FinishedRvAdapter mAdapter;
    private long mLastOrderId = 0;
    private Context mContext;

    @BindView(R.id.tv_date)
    TextView dateText;
    @BindView(R.id.tv_order_count)
    TextView orderCountText;
    @BindView(R.id.tv_cup_count)
    TextView cupCountText;
    /**
     * 订单详情页UI组件
     */
    @BindView(R.id.tv_shop_order_id)
    TextView shopOrderNumText;
    @BindView(R.id.order_id)
    TextView orderIdTxt;
    @BindView(R.id.receiver_name)
    TextView receiveNameTxt;
    @BindView(R.id.receiver_phone)
    TextView receivePhoneTxt;
    @BindView(R.id.receiver_address)
    TextView receiveAddressTxt;
    @BindView(R.id.tv_order_distance)
    TextView orderDistanceText;
    @BindView(R.id.items_container_layout)
    LinearLayout itemsContainerLayout;
    @BindView(R.id.user_remark)
    TextView userRemarkTxt;
    @BindView(R.id.csad_remark)
    TextView csadRemarkTxt;
    /**
     * 订单详情
     */
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
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_finished_order, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }


    private void initViews() {
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(8, mContext), false));
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(true);

        mAdapter = new FinishedRvAdapter(getActivity());
        pullLoadMoreRecyclerView.setAdapter(mAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateText.setText(sdf.format(new Date()));

    }

    @Override
    public void showEmpty(boolean isNeedToShow) {
        if(isNeedToShow){
            tvEmpty.setVisibility(View.VISIBLE);
        }else{
            tvEmpty.setVisibility(View.GONE);
        }
    }


    @Override
    public void bindDataToView(List<OrderBean> list) {
        if(list!=null && list.size()>0){
            showEmpty(false);
            mAdapter.setData(list);
        }else{
            showEmpty(true);
        }

    }

    @Override
    public void appendListData(List<OrderBean> list) {
        mAdapter.addData(list);
    }

    @Override
    public void bindAmountDataToView(int ordersAmount, int cupsAmount) {
        if(orderCountText!=null && cupCountText!=null){
            orderCountText.setText(String.valueOf(ordersAmount));
            cupCountText.setText(String.valueOf(cupsAmount));
        }
    }


    @Override
    public void saveLastOrderId() {
        if (mAdapter.list.size() > 0) {
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
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(), promptStr);
    }

    private void updateDetailView(final OrderBean order) {
        if (order == null) {
            orderIdTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            orderDistanceText.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            itemsContainerLayout.removeAllViews();
        } else {
            shopOrderNumText.setText(OrderHelper.getShopOrderSn(order));
            orderIdTxt.setText(order.getOrderSn());
            //服务时效
            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            orderDistanceText.setText(OrderHelper.getDistanceFormat(order.getOrderDistance()));
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
        }

    }

    //填充item数据
    private void fillItemListData(LinearLayout ll, final OrderBean order) {
        if (order == null) {
            return;
        }
        ll.removeAllViews();
        List<ItemContentBean> items = order.getItems();
        for (ItemContentBean item : items) {
            TextView tv1 = new TextView(mContext);
            tv1.setId(R.id.item_name);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(9);
            tv1.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv1.setTextColor(mContext.getResources().getColor(R.color.font_black));
            TextView tv2 = new TextView(mContext);
            tv2.setId(R.id.item_num);
            tv2.setText("x  " + item.getQuantity());
            tv2.getPaint().setFakeBoldText(true);
            tv2.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));

            RelativeLayout rl = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2, mContext);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2, mContext);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            String dingzhi = OrderHelper.getLabelStr(item.getRecipeFittingsList());
            if (!TextUtils.isEmpty(dingzhi)) {
                TextView tv5 = new TextView(mContext);
                tv5.setId(R.id.item_flag);
                tv5.setText(dingzhi);
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(), R.mipmap.flag_ding);
                drawable.setBounds(0, 1, OrderHelper.dip2Px(14, mContext), OrderHelper.dip2Px(14, mContext));
                tv5.setCompoundDrawablePadding(OrderHelper.dip2Px(4, mContext));
                tv5.setCompoundDrawables(drawable, null, null, null);
                tv5.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
                tv5.setTextColor(mContext.getResources().getColor(R.color.font_black));
                RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp5.addRule(RelativeLayout.BELOW, tv1.getId());
                lp5.addRule(RelativeLayout.ALIGN_LEFT, tv1.getId());
                tv5.setLayoutParams(lp5);
                rl.addView(tv5);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2, mContext);
            ll.addView(rl, lp);
        }

        TextView tv6 = new TextView(mContext);
        tv6.setText(mContext.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        tv6.setGravity(Gravity.CENTER);
        tv6.getPaint().setFakeBoldText(true);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp6.topMargin = OrderHelper.dip2Px(30, mContext);
        tv6.setLayoutParams(lp6);
        ll.addView(tv6, lp6);

        ll.invalidate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFinishedPresenter.loadOrderAmount();
        mFinishedPresenter.loadFinishedOrders(0,false);

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Subscribe
    public void onUpdateFinishedOrderDetailEvent(UpdateFinishedOrderDetailEvent event) {
        updateDetailView(event.orderBean);
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

    @Override
    public void onRefresh() {

    }


    @Override
    public void onLoadMore() {
        mFinishedPresenter.loadFinishedOrders(mLastOrderId,true);
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

    class FineshedTaskRunnable implements Runnable {
        @Override
        public void run() {
            mFinishedPresenter.loadOrderAmount();
            mFinishedPresenter.loadFinishedOrders(0,false);
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


    @OnClick({R.id.ll_user_remark, R.id.ll_csad_remark})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_remark:
                //用户备注
                new InfoDetailDialog(getActivity()).show(userRemarkTxt.getText().toString());
                break;
            case R.id.ll_csad_remark:
                //客服备注
                new InfoDetailDialog(getActivity()).show(csadRemarkTxt.getText().toString());
                break;
        }
    }

}
