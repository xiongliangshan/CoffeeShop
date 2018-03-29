package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.common.ProductHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.DBChangeEvent;
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
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProducingFragment extends BaseFragment implements ProducingView<OrderBean>,
        ProducingRvAdapter.ProducingCallback,DetailView.ActionCallback {


    private final String TAG = "ProducingFragment";

    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_finish_all)
    Button btnFinishAll;
    @BindView(R.id.btn_finish_one)
    Button btnFinishOne;
    @BindView(R.id.detail_view)
    DetailView detailView;
    private ProducingPresenter mProducingPresenter;

    @BindView(R.id.rv_producing)
    RecyclerView mRecyclerView;
    private Unbinder unbinder;
    private ProducingRvAdapter mAdapter;
    private Context mContext;

    private GradientDrawable mGroupDrawable; //生产完成按钮倒计时
    public List<OrderBean> allOrderList = new ArrayList<>();

    private Handler mHandler;
    private ProducingTaskRunnable mRunnable;

    private Timer timer = new Timer();
    private long currentOrderId = 0;
    private TimerTask timerTask;
    //延迟时间
    private final int reHandlerTime = 1 * 1000;
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
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1 :
                        dynamicChangeTime();
                }
            }
        };
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


        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        if(user.isOpenFulfill()){
            btnFinishAll.setVisibility(View.GONE);
            btnFinishOne.setVisibility(View.VISIBLE);
        }else{
            btnFinishAll.setVisibility(View.VISIBLE);
            btnFinishOne.setVisibility(View.GONE);
        }
        mGroupDrawable= (GradientDrawable) btnFinishOne.getBackground();
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
        if (order == null) {
        } else {
            currentOrderId = order.getId();
        }
    }


    @Override
    public boolean removeItemFromList(int id) {
        return mAdapter.removeOrderFromList(id);
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
        if(timer!=null){
            timer.cancel();
        }
        if(timerTask != null){
            timerTask.cancel();
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

        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        if(user.isOpenFulfill()){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            };
            timer.schedule(timerTask, reHandlerTime, reHandlerTime);
        }
    }

    private void dynamicChangeTime(){
        for(OrderBean orderBean : allOrderList){
            try {
                TextView huanghe = (TextView) mRecyclerView.findViewWithTag("huanghe" + orderBean.getId());
                if(huanghe != null){
                    //修改订单详情的时间倒计时
                    if(orderBean.getId() == currentOrderId){
                        detailView.updateTime(orderBean);
                    }
                    //订单列表的倒计时
                    Map<String,Object> productCapacity = ProductHelper.getProduct(CSApplication.getInstance());
                    List<ItemContentBean> icbcList = orderBean.getItems();
                    int coldCups = 0; //冷热数量
                    int hotCups = 0;
                    int productTime = 0;//计算产能时间
                    for (ItemContentBean itemContentBean : icbcList) {
                        if (itemContentBean.getColdHotProperty() == 1) {
                            coldCups++;
                        } else {
                            hotCups++;
                        }
                        if (productCapacity.containsKey(itemContentBean.getProduct())) {
                            productTime += itemContentBean.getQuantity() * Integer.getInteger(productCapacity.get(itemContentBean.getProduct()).toString(), 1) * 30 * 1000;
                            System.out.println("商品=" + itemContentBean.getProduct() + "productTime=" + productTime);
                        } else {
                            productTime += itemContentBean.getQuantity() * 1 * 30 * 1000;
                        }
                    }
//                    System.out.println("icbcList.size()=" + icbcList.size());
//                    System.out.println("冷杯子=" + coldCups + "热杯子=" + hotCups);
                    int coldBox = coldCups / 4 + (coldCups % 4) > 0 ? 1 : 0;
                    int hotBox = hotCups / 4 + (hotCups % 4) > 0 ? 1 : 0;
//                    System.out.println("冷盒子=" + coldBox + "热盒子=" + hotBox);
                    OrderBean orderBeanLoca =  OrderUtils.with().getOrderById(orderBean.getId());
                    long currentTimeMillis = System.currentTimeMillis();
//                    System.out.println("startProductTime=" + orderBeanLoca.getStartProduceTime() + "productTime=" + productTime + "盒子=" + (hotBox + coldBox));
                    long timeMinus = orderBeanLoca.getStartProduceTime() + productTime + (hotBox + coldBox) * 2 * 60 * 1000 - currentTimeMillis;
                    long timeOverTime = orderBeanLoca.getInstanceTime() - currentTimeMillis;
                    if (timeMinus > 0) {
                        long time = timeMinus / 1000;
                        huanghe.setText(time / 60 + "分" + time % 60 + "秒" + "内生产完成");
                        huanghe.setTextColor(mContext.getResources().getColor(R.color.green1));
                    } else if (timeOverTime > 0) {
                        long time = Math.abs(timeMinus) / 1000;
                        huanghe.setText("生产超时" + time / 60 + "分" + time % 60 + "秒");
                        huanghe.setTextColor(mContext.getResources().getColor(R.color.tab_orange));
                    } else {
                        long time = Math.abs(timeOverTime) / 1000;
                        huanghe.setText("送达超时" + time / 60 + "分" + time % 60 + "秒");
                        huanghe.setTextColor(mContext.getResources().getColor(R.color.red1));
                    }
                }
            } catch (Exception e) {
                LogUtil.v(TAG, "refresh time has problem ecp:" + e.getMessage());
                Logger.getLogger().log("refresh time has problem ecp:" + e.getMessage());
            }
        }
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls", "producingFragment is InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        if (timerTask != null) {
            timerTask.cancel();
            if (timer != null) {
                timer.purge();
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBChangeEvent(DBChangeEvent event){
        List<OrderBean> dbProducingOrders = OrderUtils.with().queryByProduceStatus(OrderStatus.PRODUCING);
        if(dbProducingOrders!=null && dbProducingOrders.size()>0){
            bindDataToView(dbProducingOrders);
        }

    }


    //订单撤销事件
    @Subscribe
    public void onRevokeEvent(RevokeEvent event) {
        if (event.orderBean == null) {
            LogUtil.e("xls", "onRevokeEvent orderBean = null");
            return;
        }
        if (removeItemFromList((int) event.orderBean.getId())) {
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

    @OnClick({R.id.btn_search, R.id.btn_finish_all, R.id.btn_finish_one})
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
            case R.id.btn_finish_one:
//                生产完成
                /** 倒计时5秒，一次1秒 */
                new CountDownTimer(1 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub
                        btnFinishOne.setEnabled(false);
//                        btnFinishOne.setText(millisUntilFinished/1000+"");
//                        btnFinishOne.setTextSize(32);
                        mGroupDrawable.setColor(CSApplication.getInstance().getResources().getColor(R.color.gray3));
                        mProducingPresenter.doFinishProducedFulfill();
                    }
                    @Override
                    public void onFinish() {
                        mGroupDrawable.setColor(CSApplication.getInstance().getResources().getColor(R.color.green1));
//                        btnFinishOne.setTextSize(32);
//                        btnFinishOne.setText("生产完成");
                        btnFinishOne.setEnabled(true);
                    }
                }.start();
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
