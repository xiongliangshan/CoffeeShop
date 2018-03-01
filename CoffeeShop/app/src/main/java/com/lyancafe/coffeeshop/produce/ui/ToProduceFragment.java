package com.lyancafe.coffeeshop.produce.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.NotNeedProduceEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.event.StartProduceBatchEvent;
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
import com.lyancafe.coffeeshop.utils.VSpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.DetailView;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.lyancafe.coffeeshop.produce.ui.ListMode.NORMAL;
import static com.lyancafe.coffeeshop.produce.ui.ListMode.SELECT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToProduceFragment extends BaseFragment implements ToProduceView<OrderBean>,
        ToProduceRvAdapter.ToProduceCallback,DetailView.ActionCallback {


    private RecyclerView mRecyclerView;
    private ConstraintLayout batchLayout;
    private Button summarizeBtn;
    private Button batchSelectBtn;
    private Button cancelBtn;
    private ConstraintLayout searchLayout;
    private EditText etSearchKey;
    private Button btnSearch;
    private DetailView detailView;

    private ToProduceRvAdapter mAdapter;
    private SummarizeAdapter summarizeAdapter;
    private Context mContext;
    private List<OrderBean> allOrderList = new ArrayList<>();
    private Handler mHandler;
    private ToProduceTaskRunnable mRunnable;

    private ToProducePresenter mToProducePresenter;

    private MyClickListener myClickListener;

    //当前订单模式
    private OrderMode currentMode = OrderMode.NORMAL;

    private SpaceItemDecoration spaceItemDecoration;
    private VSpaceItemDecoration vSpaceItemDecoration;


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

        spaceItemDecoration = new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getContext()), false);
        vSpaceItemDecoration = new VSpaceItemDecoration(OrderHelper.dip2Px(12, getContext()));
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

        searchLayout = (ConstraintLayout) view.findViewById(R.id.cl_search);
        etSearchKey = (EditText) view.findViewById(R.id.et_search_key);
        btnSearch = (Button) view.findViewById(R.id.btn_search);

        detailView = (DetailView) view.findViewById(R.id.detail_view);
        detailView.setCallback(this);

        setListener();

        mAdapter = new ToProduceRvAdapter(getActivity());
        mAdapter.setCallback(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(spaceItemDecoration);
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


        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        if(user.isOpenFulfill()){
            summarizeBtn.setVisibility(View.GONE);
            batchSelectBtn.setVisibility(View.GONE);
        }else{
            summarizeBtn.setVisibility(View.VISIBLE);
            batchSelectBtn.setVisibility(View.VISIBLE);
        }
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
        if (currentMode == OrderMode.SUMMARIZE) {
            List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(allOrderList);
            summarizeAdapter.setData(OrderHelper.caculateGroupList(groups));
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartProduceEvent(StartProduceEvent event) {
        /**
         * 开始生产&&打印
         */
        mToProducePresenter.doStartProduce(event.order.getId(), event.order.getWxScan());
        PrintFace.getInst().startPrintWholeOrderTask(event.order);
    }

    /**
     * 批量开始生产打印
     */
    @Subscribe
    public void onStartProduceBatchEvent(StartProduceBatchEvent event){
        if (event.orders==null || event.orders.size()==0) {
            showToast("汇总数据异常");
            return;
        }
        mToProducePresenter.doStartBatchProduce(event.orders);
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
        allOrderList.clear();
        allOrderList.addAll(mAdapter.getList());
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void removeItemsFromList(List<Long> ids) {
        mAdapter.removeOrdersFromList(ids);
        allOrderList.clear();
        allOrderList.addAll(mAdapter.getList());

        if(currentMode==OrderMode.SUMMARIZE){
            List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(allOrderList);
            List<SummarizeGroup> resultGroups = OrderHelper.caculateGroupList(groups);

            renderSummarizeUI(resultGroups);
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
                //默认选中最紧急的一批订单
                mAdapter.selectDefaultOrders();
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
                        Logger.getLogger().log("点击 批量选择");
                        if(allOrderList.size()<2){
                            ToastUtil.show(getContext(),"订单数少于2，无法批量");
                            return;
                        }
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
                        Logger.getLogger().log("点击 批量开始 ， 总数: "+selectedList.size()+",订单集合:"+OrderHelper.getOrderIds(selectedList));
                        mToProducePresenter.doStartBatchProduce(selectedList);
                    }

                    break;
                case R.id.btn_cancel:
                    setMode(NORMAL);
                    cancelBtn.setVisibility(View.GONE);
                    batchSelectBtn.setText(R.string.batch_select);
                    Logger.getLogger().log("取消 批量选择");
                    break;
                case R.id.btn_search:
                    search();
                    break;
                case R.id.btn_summarize:
                    if("汇总模式".equals(summarizeBtn.getText())){
                        if(allOrderList.size()<1){
                            ToastUtil.show(getContext(),"没有可以汇总的订单!");
                            return;
                        }
                        summarizeBtn.setText("详单模式");
                        switchMode(OrderMode.SUMMARIZE);
                    }else if("详单模式".equals(summarizeBtn.getText())){
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
            detailView.setVisibility(View.GONE);
            long start = System.currentTimeMillis();
            List<SummarizeGroup> groups = OrderHelper.splitOrdersToGroup(allOrderList);
            List<SummarizeGroup> resultGroups = OrderHelper.caculateGroupList(groups);
            long end = System.currentTimeMillis();
            LogUtil.d("xiong","计算数据所用时间:"+(end - start));

            renderSummarizeUI(resultGroups);
            Logger.getLogger().log("切换到 汇总模式");
        }else{
            //详单模式
            detailView.setVisibility(View.VISIBLE);
            renderNormalUI();
            Logger.getLogger().log("切换到 详单模式");
        }

        this.currentMode = mode;
    }

    /**
     * 渲染普通详单模式的UI
     */
    private void renderNormalUI(){
        batchLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
        if(mAdapter==null){
            mAdapter = new ToProduceRvAdapter(getActivity());
            mAdapter.setCallback(this);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false));
        mRecyclerView.removeItemDecoration(vSpaceItemDecoration);
        mRecyclerView.addItemDecoration(spaceItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 渲染汇总模式的UI
     * @param groups
     */
    private void renderSummarizeUI(List<SummarizeGroup> groups){
        batchLayout.setVisibility(View.INVISIBLE);
        searchLayout.setVisibility(View.INVISIBLE);
        if(summarizeAdapter==null){
            summarizeAdapter = new SummarizeAdapter(getContext());
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.removeItemDecoration(spaceItemDecoration);
        mRecyclerView.addItemDecoration(vSpaceItemDecoration);
        mRecyclerView.setAdapter(summarizeAdapter);
        summarizeAdapter.setData(groups);

    }




    // 执行搜索
    private void search(){
        String searchKey = etSearchKey.getText().toString();
        Logger.getLogger().log("待生产搜索, key = "+searchKey);
        if(TextUtils.isEmpty(searchKey)){
            mAdapter.setSearchData(allOrderList);
            return;
        }
        try{
            searchOrder(Integer.parseInt(searchKey));
        }catch (NumberFormatException e){
            showToast("数据太大或者类型不对");
            return;
        }

        MyUtil.hideKeyboard(etSearchKey);
        etSearchKey.setText("");
    }

    //搜索
    public void searchOrder(final int shopOrderNo){
        final List<OrderBean> result = new ArrayList<>();
        Observable.fromIterable(allOrderList)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<OrderBean>() {
                    @Override
                    public boolean test(@NonNull OrderBean orderBean) throws Exception {
                        return orderBean.getShopOrderNo()==shopOrderNo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull OrderBean orderBean) {
                        result.add(orderBean);
                        mAdapter.setSearchData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if(result.size()==0){
                            ToastUtil.show(getContext(),"没有搜到目标订单");
                            Logger.getLogger().log("待生产-没有搜到目标订单 "+shopOrderNo);
                        }
                    }
                });

    }




    class ToProduceTaskRunnable implements Runnable {
        @Override
        public void run() {
            mToProducePresenter.loadToProduceOrders();
        }
    }

}
