package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.common.PrintHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.NaiGaiEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.NotNeedProduceEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenter;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.lyancafe.coffeeshop.produce.ui.ListMode.NORMAL;
import static com.lyancafe.coffeeshop.produce.ui.ListMode.SELECT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToProduceFragment extends BaseFragment implements ToProduceView {


    @BindView(R.id.rv_to_produce)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_naigai_layout)
    ConstraintLayout naigaiLayout;
    @BindView(R.id.tv_amount_hongyu)
    TextView tvHongyu;
    @BindView(R.id.tv_amount_moli)
    TextView tvMoli;
    @BindView(R.id.cl_batch_layout)
    ConstraintLayout batchLayout;
    @BindView(R.id.btn_batch_select)
    Button batchSelectBtn;
    @BindView(R.id.btn_cancel)
    Button cancelBtn;
    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.btn_search)
    Button btnSearch;

    private Unbinder unbinder;
    private ToProduceRvAdapter mAdapter;
    private Context mContext;
    public List<OrderBean> allOrderList = new ArrayList<>();
    private Handler mHandler;
    private ToProduceTaskRunnable mRunnable;

    private ToProducePresenter mToProducePresenter;
    private LoadingDialog mLoadinngDlg;

    public ToProduceFragment() {

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
        mToProducePresenter = new ToProducePresenterImpl(this.getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_to_produce, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }


    private void initViews() {
        mAdapter = new ToProduceRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);

        etSearchKey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_UP){
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToProducePresenter.loadToProduceOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Subscribe
    public void onNaiGaiEvent(NaiGaiEvent event) {
        showNaiGaiAmount(event.map);
    }

    @Override
    public void showNaiGaiAmount(Map<String, Integer> map) {
        if (naigaiLayout == null) {
            return;
        }
        int hongyu = map.get(CSApplication.getInstance().getString(R.string.coffee_hongyu));
        int moli = map.get(CSApplication.getInstance().getString(R.string.coffee_moli));
        if (hongyu == 0 && moli == 0) {
            naigaiLayout.setVisibility(View.GONE);
        } else {
            naigaiLayout.setVisibility(View.VISIBLE);
            tvHongyu.setText(hongyu + "杯");
            tvMoli.setText(moli + "杯");
        }

    }

    @Override
    public void bindDataToView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        if (isVisible) {
            if (list != null && list.size() > 1) {
                batchLayout.setVisibility(View.VISIBLE);
            } else {
                batchLayout.setVisibility(View.GONE);
            }
            mAdapter.setData(list);
        }
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(), promptStr);
    }

    @Override
    public void showLoading() {
        if (mLoadinngDlg == null) {
            mLoadinngDlg = new LoadingDialog(getContext());
        }
        if (!mLoadinngDlg.isShowing()) {
            mLoadinngDlg.show();
        }

    }

    @Override
    public void dismissLoading() {
        if (mLoadinngDlg != null && mLoadinngDlg.isShowing()) {
            mLoadinngDlg.dismiss();
        }
    }


    @Override
    public void showStartProduceConfirmDialog(final OrderBean orderBean) {
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(getActivity(), R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                //请求服务器改变该订单状态，由 待生产--生产中
                mToProducePresenter.doStartProduce(orderBean.getId(), orderBean.getWxScan());
                PrintHelper.getInstance().startPrintWholeOrderTask(orderBean);

            }
        });
        grabConfirmDialog.setContent("订单 " + orderBean.getOrderSn() + " 开始生产？");
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
        if (mHandler != null) {
            mHandler = null;
        }
    }


    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls", "ToproduceFragment is Visible");
        if (!isResumed()) {
            return;
        }
        mRunnable = new ToProduceTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls", "ToproduceFragment is InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    //新订单消息触发事件
    @Subscribe
    public void onNewOrderComing(NewOderComingEvent event) {
        LogUtil.d(LogUtil.TAG_PRODUCE, "onNewOrderComming : orderId = " + event.orderId);
        if (isResumed()) {
            mToProducePresenter.loadToProduceOrders();
        }

    }

    //订单撤销事件
    @Subscribe
    public void onRevokeEvent(RevokeEvent event) {
        if (event.orderBean == null) {
            LogUtil.e("xls", "onRevokeEvent orderBean = null");
            return;
        }
        if (event.orderBean.getProduceStatus() == 4000) {
            removeItemFromList((int) event.orderBean.getId());
            EventBus.getDefault().postSticky(new ChangeTabCountByActionEvent(OrderAction.REVOKEORDER, 0, 1));
        }


    }

    /**
     * 点击开始生产按钮事件
     *
     * @param event
     */
    @Subscribe
    public void onStartProduceEvent(StartProduceEvent event) {
        showStartProduceConfirmDialog(event.order);
    }

    /**
     * 点击无需生产按钮
     * @param event
     */
    @Subscribe
    public void onNotNeedProduce(NotNeedProduceEvent event){
        mToProducePresenter.doNoPruduce(event.order.getId());
        PrintHelper.getInstance().startPrintOnlyBoxTask(event.order);
    }


    @Override
    public void removeItemFromList(int id) {
        mAdapter.removeOrderFromList(id);
    }

    @Override
    public void removeItemsFromList(List<Long> ids) {
        mAdapter.removeOrdersFromList(ids);
    }

    //订单状态改变后刷新列表UI
    public void refreshListForStatus(long orderId, int status) {
        if (mAdapter == null) {
            return;
        }
        for (int i = 0; i < mAdapter.list.size(); i++) {
            OrderBean order = mAdapter.list.get(i);
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

    @Override
    public void setMode(ListMode mode) {
        mAdapter.curMode = mode;
        mAdapter.notifyDataSetChanged();
        switch (mode) {
            case NORMAL:
                batchSelectBtn.setText(R.string.batch_select);
                cancelBtn.setVisibility(View.GONE);
                break;
            case SELECT:
                batchSelectBtn.setText(R.string.batch_start);
                cancelBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick({R.id.btn_batch_select, R.id.btn_cancel,R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_batch_select:
                if (getString(R.string.batch_select).equals(batchSelectBtn.getText().toString())) {
                    //点击批量选择
                    mAdapter.selectMap.clear();
                    setMode(SELECT);

                } else {
                    //点击批量开始
                    LogUtil.d("xls", "被选中的订单:");
                    List<OrderBean> selectedList = mAdapter.getBatchOrders();
                    if (selectedList.size() == 0) {
                        showToast("未选中订单");
                        return;
                    }

                    PrintHelper.getInstance().printBatch(selectedList);
                    List<Long> orderIds = OrderHelper.getIdsFromOrders(selectedList);
                    mToProducePresenter.doStartBatchProduce(orderIds);


                }

                break;
            case R.id.btn_cancel:
                setMode(NORMAL);
                cancelBtn.setVisibility(View.GONE);
                batchSelectBtn.setText(R.string.batch_select);
                break;
            case R.id.btn_search:
                search();
                break;
        }
    }

    // 执行搜索
    private void search(){
        String searchKey = etSearchKey.getText().toString();
        if(TextUtils.isEmpty(searchKey)){
            mAdapter.setSearchData(mAdapter.tempList);
            return;
        }
        mAdapter.searchOrder(Integer.parseInt(searchKey));
        MyUtil.hideKeyboard(etSearchKey);
        etSearchKey.setText("");
    }




    class ToProduceTaskRunnable implements Runnable {
        @Override
        public void run() {
            mToProducePresenter.loadToProduceOrders();
        }
    }
}
