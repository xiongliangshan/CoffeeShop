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

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.NotNeedProduceEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.printer.PrintFace;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenter;
import com.lyancafe.coffeeshop.produce.presenter.ToProducePresenterImpl;
import com.lyancafe.coffeeshop.produce.view.ToProduceView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lyancafe.coffeeshop.produce.ui.ListMode.NORMAL;
import static com.lyancafe.coffeeshop.produce.ui.ListMode.SELECT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToProduceFragment extends BaseFragment implements ToProduceView<OrderBean>,ToProduceRvAdapter.ToProduceCallback {


    RecyclerView mRecyclerView;
    ConstraintLayout batchLayout;
    Button summarizeBtn;
    Button batchSelectBtn;
    Button cancelBtn;
    EditText etSearchKey;
    Button btnSearch;

    private ToProduceRvAdapter mAdapter;
    private SummarizeAdapter summarizeAdapter;
    private Context mContext;
    public List<OrderBean> allOrderList = new ArrayList<>();
    private Handler mHandler;
    private ToProduceTaskRunnable mRunnable;

    private ToProducePresenter mToProducePresenter;

    private MyClickListener myClickListener;

    //当前订单模式
    private OrderMode currentMode = OrderMode.NORMAL;

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
        myClickListener = new MyClickListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_to_produce, container, false);
        initViews(contentView);
        return contentView;
    }


    private void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_to_produce);
        batchLayout = (ConstraintLayout) view.findViewById(R.id.cl_batch_layout);
        summarizeBtn = (Button) view.findViewById(R.id.btn_summarize);
        batchSelectBtn = (Button) view.findViewById(R.id.btn_batch_select);
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        etSearchKey = (EditText) view.findViewById(R.id.et_search_key);
        btnSearch = (Button) view.findViewById(R.id.btn_search);

        setListener();

        mAdapter = new ToProduceRvAdapter(getActivity());
        mAdapter.setCallback(this);
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

    private void setListener(){
        summarizeBtn.setOnClickListener(myClickListener);
        batchSelectBtn.setOnClickListener(myClickListener);
        cancelBtn.setOnClickListener(myClickListener);
        btnSearch.setOnClickListener(myClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToProducePresenter.loadToProduceOrders();
    }




    @Override
    public void bindDataToView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list);
    }

    @Override
    public void updateBatchUI(int size) {
        if(size>=2){
            batchLayout.setVisibility(View.VISIBLE);
        }else{
            batchLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void showStartProduceConfirmDialog(final OrderBean orderBean) {
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(getActivity(), R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                //请求服务器改变该订单状态，由 待生产--生产中
                mToProducePresenter.doStartProduce(orderBean.getId(), orderBean.getWxScan());
                PrintFace.getInst().startPrintWholeOrderTask(orderBean);

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
        PrintFace.getInst().startPrintOnlyBoxTask(event.order);
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

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
                        mToProducePresenter.doStartBatchProduce(selectedList);
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
                case R.id.btn_summarize:
                    if("汇总模式".equals(summarizeBtn.getText())){
                        ToastUtil.show(getContext(),"开启汇总模式");
                        summarizeBtn.setText("详单模式");
                        switchMode(OrderMode.SUMMARIZE);
                    }else if("详单模式".equals(summarizeBtn.getText())){
                        ToastUtil.show(getContext(),"详单模式");
                        summarizeBtn.setText("汇总模式");
                        switchMode(OrderMode.NORMAL);
                    }

                    break;
            }
        }
    }


    //切换模式
    private void switchMode(OrderMode mode){
        if(mode==OrderMode.SUMMARIZE){
            //汇总模式
            List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(mAdapter.tempList);
            OrderHelper.caculateGroupList(groups);
        }else{
            //详单模式
        }

        this.currentMode = mode;
    }


    // 执行搜索
    private void search(){
        String searchKey = etSearchKey.getText().toString();
        Logger.getLogger().log("待生产搜索 "+searchKey);
        if(TextUtils.isEmpty(searchKey)){
            mAdapter.setSearchData(mAdapter.tempList);
            return;
        }
        try{
            mAdapter.searchOrder(Integer.parseInt(searchKey));
        }catch (NumberFormatException e){
            showToast("数据太大或者类型不对");
            return;
        }

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
