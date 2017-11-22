package com.lyancafe.coffeeshop.shop.ui;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.event.UpdateExceptionalDetailEvent;
import com.lyancafe.coffeeshop.shop.presenter.ExceptionalPresenter;
import com.lyancafe.coffeeshop.shop.presenter.ExceptionalPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.ExceptionalView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExceptionalFragment extends BaseFragment implements ExceptionalView<ExceptionalOrder>, PullLoadMoreRecyclerView.PullLoadMoreListener {


    Unbinder unbinder;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.plmgv_order_list)
    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    @BindView(R.id.tv_shop_order_id)
    TextView tvShopOrderId;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.contant_custom_info)
    TextView contantCustomInfo;
    @BindView(R.id.tv_receiver_name)
    TextView tvReceiverName;
    @BindView(R.id.tv_receiver_phone)
    TextView tvReceiverPhone;
    @BindView(R.id.constant_receiver_address)
    TextView constantReceiverAddress;
    @BindView(R.id.tv_receiver_address)
    TextView tvReceiverAddress;
    @BindView(R.id.constant_order_distance)
    TextView constantOrderDistance;
    @BindView(R.id.tv_order_distance)
    TextView tvOrderDistance;
    @BindView(R.id.items_container_layout)
    LinearLayout itemsContainerLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.contant_user_remark)
    TextView contantUserRemark;
    @BindView(R.id.tv_user_remark)
    TextView tvUserRemark;
    @BindView(R.id.ll_user_remark)
    LinearLayout llUserRemark;
    @BindView(R.id.contant_csad_remark)
    TextView contantCsadRemark;
    @BindView(R.id.tv_csad_remark)
    TextView tvCsadRemark;
    @BindView(R.id.ll_csad_remark)
    LinearLayout llCsadRemark;
    @BindView(R.id.btn_dispatch_to_haikui)
    Button btnDispatchToHaikui;
    @BindView(R.id.btn_dispatch_to_own)
    Button btnDispatchToOwn;
    @BindView(R.id.detail_root_view)
    LinearLayout detailRootView;

    private ExceptionalRvAdapter mAdapter;

    private ExceptionalPresenter mExceptionalPresenter;

    private Context mContext;

    private Handler mHandler;
    private ExceptionalTaskRunnable mRunnable;
    private LoadingDialog mLodingDlg;

    private OrderBean mOrder;

    public ExceptionalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExceptionalPresenter = new ExceptionalPresenterImpl(getContext(),this);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_exceptional, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }


    private void initViews() {
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(8, getContext()), false));
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(true);

        mAdapter = new ExceptionalRvAdapter(getContext());
        pullLoadMoreRecyclerView.setAdapter(mAdapter);

    }

    @Subscribe
    public void onUpdateExceptionalOrderDetailEvent(UpdateExceptionalDetailEvent event) {
        updateDetailView(event.exceptionalOrder);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void bindDataToView(List<ExceptionalOrder> list) {
        if(list!=null && list.size()>0){
            showEmpty(false);
            mAdapter.setData(list);
        }else{
            showEmpty(true);
        }
    }

    @Override
    public void showLoading() {
        if (mLodingDlg == null) {
            mLodingDlg = new LoadingDialog(getActivity());
        }
        if (!mLodingDlg.isShowing()) {
            mLodingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLodingDlg != null && mLodingDlg.isShowing()) {
            mLodingDlg.hide();
        }
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.show(getActivity(), promptStr);
    }

    @Override
    public void showEmpty(boolean isNeedToShow) {
        if(tvEmpty!=null){
            tvEmpty.setVisibility(isNeedToShow?View.VISIBLE:View.GONE);
        }
    }

    @Override
    public void removeItemFromList(long orderId) {
        mAdapter.removeOrderFromList(orderId);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (!isResumed()) {
            return;
        }
        mRunnable = new ExceptionalTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        dismissLoading();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

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

    private void updateDetailView(final OrderBean order) {
        mOrder = order;
        if (order == null) {
            tvOrderId.setText("");
            tvReceiverName.setText("");
            tvReceiverPhone.setText("");
            tvReceiverAddress.setText("");
            tvOrderDistance.setText("");
            tvUserRemark.setText("");
            tvCsadRemark.setText("");
            itemsContainerLayout.removeAllViews();
        } else {
            tvShopOrderId.setText(OrderHelper.getShopOrderSn(order));
            tvOrderId.setText(order.getOrderSn());
            //服务时效
            tvReceiverName.setText(order.getRecipient());
            tvReceiverPhone.setText(order.getPhone());
            tvReceiverAddress.setText(order.getAddress());
            tvOrderDistance.setText(OrderHelper.getDistanceFormat(order.getOrderDistance()));
            fillItemListData(itemsContainerLayout, order);
            tvUserRemark.setText(order.getNotes());
            tvCsadRemark.setText(order.getCsrNotes());
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
            tv1.setTextSize(getResources().getDimension(R.dimen.content_item_text_size));
            tv1.setTextColor(getResources().getColor(R.color.font_black));
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

            String dingzhi = item.getRecipeFittings();
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

    @OnClick({R.id.btn_dispatch_to_haikui, R.id.btn_dispatch_to_own})
    public void onViewClicked(View view) {
        if(mOrder==null){
            showToast("订单信息丢失,请重新选中订单");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_dispatch_to_haikui:
                mExceptionalPresenter.doRePush(mOrder.getId(),9);
                break;
            case R.id.btn_dispatch_to_own:
                mExceptionalPresenter.doRePush(mOrder.getId(),4);
                break;
        }
    }

    class ExceptionalTaskRunnable implements Runnable {
        @Override
        public void run() {
            mExceptionalPresenter.loadExceptionalOrders();
        }
    }
}
