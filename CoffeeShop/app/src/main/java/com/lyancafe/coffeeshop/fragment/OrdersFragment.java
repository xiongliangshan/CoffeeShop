package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.AssignOrderActivity;
import com.lyancafe.coffeeshop.activity.CommentActivity;
import com.lyancafe.coffeeshop.activity.HomeActivity;
import com.lyancafe.coffeeshop.activity.LoginActivity;
import com.lyancafe.coffeeshop.activity.PrintOrderActivity;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.adapter.OrderListViewAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.CancelOrderEvent;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.ClickCommentEvent;
import com.lyancafe.coffeeshop.event.CommentCountEvent;
import com.lyancafe.coffeeshop.event.CommitIssueOrderEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.NewOderComingEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdatePrintStatusEvent;
import com.lyancafe.coffeeshop.event.UpdateTabOrderListCountEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.utils.Urls;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.ListTabButton;
import com.lyancafe.coffeeshop.widget.PromptDialog;
import com.lyancafe.coffeeshop.widget.ReportWindow;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment implements View.OnClickListener{

    private static final String TAG  ="OrdersFragment";
    private View mContentView;
    private Context mContext;
    public static int subTabIndex = TabList.TAB_TOPRODUCE;

    private ListTabButton toDoTab;
    private ListTabButton doingTab;
    private ListTabButton haveDoneTab;
    private ListTabButton deliveringTab;
    private ListTabButton deliveryFinishedTab;

    private TextView shopNameText;
    private ListTabButtonListener ltbListener;

    private Spinner sortSpinner;
    private Spinner categorySpinner;

    private PullLoadMoreRecyclerView ordersGridView;
    private RecyclerView.LayoutManager mLayoutManager;
    private OrderGridViewAdapter adapter;
    private OrderListViewAdapter sfAdaper;

    private RadioGroup radioGroup;
    private RadioButton lowRb;
    private RadioButton middleRb;
    private RadioButton allRb;
    private TextView refreshbtn;
    private TextView batchHandleBtn;
    private TextView batchPromptText;
    private TextView totalQuantityTxt;
    private TextView goodCommentText;
    private TextView badCommentText;

    private int orderBy = 0;
    private int fillterInstant = 0;
    private long finishedLastOrderId = 0;
    private ReportWindow reportWindow;

    private IndoDetailListener indoDetailListener;
    private RadioButtonClicklistener radioButtonClicklistener;

    /**
     * 订单详情页UI组件
     */
    private LinearLayout detailRootView;
    private TextView orderIdTxt;
    private TextView wholeOrderText;
    private TextView orderTimeTxt;
    private TextView orderReportTxt;
    private TextView reachTimeTxt;
    private TextView produceEffectTxt;
    private TextView receiveNameTxt;
    private TextView receivePhoneTxt;
    private TextView receiveAddressTxt;
    private RelativeLayout deliverInfoContainerLayout;
    private TextView deliverNameTxt;
    private TextView deliverPhoneTxt;
    private LinearLayout itemsContainerLayout;
    private LinearLayout userRemarkLayout;
    private TextView userRemarkTxt;
    private LinearLayout csadRemarkLayout;
    private TextView csadRemarkTxt;
    private LinearLayout userCommentLayout;
    private TextView userCommentTagsText;
    private TextView userCommentContentText;
    private LinearLayout twoBtnLayout;
    private LinearLayout oneBtnLayout;
    private Button produceAndPrintBtn;
    private Button finishProduceBtn;
    private Button printOrderBtn;
    private Button moreBtn;
    private Button prevBtn;
    private Button nextBtn;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Log.d(TAG, "onAttach");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ltbListener = new ListTabButtonListener();
        indoDetailListener = new IndoDetailListener();
        radioButtonClicklistener = new RadioButtonClicklistener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(mContentView);
        initRadioGroupStatus(mContext);
        EventBus.getDefault().register(this);
        return mContentView;
    }



    private void initViews(View contentView){
        ordersGridView = (PullLoadMoreRecyclerView) contentView.findViewById(R.id.gv_order_list);
        if(LoginHelper.isSFMode()){
            mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
            sfAdaper = new OrderListViewAdapter(getActivity());
            ordersGridView.setAdapter(sfAdaper);
        }else{
            mLayoutManager = new GridLayoutManager(mContext,4,GridLayoutManager.VERTICAL,false);
            adapter = new OrderGridViewAdapter(mContext);
            ordersGridView.setAdapter(adapter);
        }

        ordersGridView.getRecyclerView().setLayoutManager(mLayoutManager);
        ordersGridView.getRecyclerView().setHasFixedSize(true);
        ordersGridView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        ordersGridView.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(8, mContext), false));

        ordersGridView.setPullRefreshEnable(false);
        ordersGridView.setPushRefreshEnable(false);
        ordersGridView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh");
            }

            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore");
                HttpHelper.getInstance().reqFinishedListData(orderBy, fillterInstant, finishedLastOrderId, new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        ordersGridView.setPullLoadMoreCompleted();
                        handleFinishedResponse(xlsResponse,call,response);
                    }
                });
            }
        });


        initTabButtons(contentView);
        initDetailView(contentView);
        initSpinner(contentView, mContext);

        shopNameText = (TextView) contentView.findViewById(R.id.tv_shop_name);
        shopNameText.setText(LoginHelper.getLoginBean(mContext).getShopName());

        batchPromptText = (TextView) contentView.findViewById(R.id.tv_batch_prompt);
        radioGroup = (RadioGroup) contentView.findViewById(R.id.rg_order_limit);
        lowRb = (RadioButton) contentView.findViewById(R.id.rb_low);
        middleRb = (RadioButton) contentView.findViewById(R.id.rb_middle);
        allRb = (RadioButton) contentView.findViewById(R.id.rb_all);
        lowRb.setOnClickListener(radioButtonClicklistener);
        middleRb.setOnClickListener(radioButtonClicklistener);
        allRb.setOnClickListener(radioButtonClicklistener);
        refreshbtn = (TextView) contentView.findViewById(R.id.btn_refresh);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
            }
        });
        batchHandleBtn = (TextView) contentView.findViewById(R.id.btn_batch_handle);
        batchHandleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batchBtnText = batchHandleBtn.getText().toString();
                if (mContext.getString(R.string.batch_handle).equals(batchBtnText)) {
                    //批量处理
                    OrderHelper.calculateToMergeOrders(adapter.list);
                    if (OrderHelper.batchList.size() < 2) {
                        ToastUtil.showToast(mContext, "没有可合并的订单");
                        OrderHelper.batchList.clear();
                        return;
                    }
                    final String content = OrderHelper.createPromptStr(mContext, OrderHelper.batchList, OrderHelper.batchHandleCupCount);
                    PromptDialog pd = new PromptDialog(mContext, R.style.PromptDialog, new PromptDialog.OnClickOKListener() {
                        @Override
                        public void onClickOK() {
                            batchPromptText.setText(content);
                            batchPromptText.setVisibility(View.VISIBLE);
                            batchHandleBtn.setText(R.string.batch_print);
                        }
                    });
                    pd.setMode(PromptDialog.Mode.SINGLE);
                    pd.setContent(content);
                    pd.show();

                } else if (mContext.getString(R.string.batch_print).equals(batchBtnText)) {
                    //批量打印
                    String content = "是否确定将 " + OrderHelper.batchOrderCount + " 单数据同时打印？";
                    PromptDialog pd = new PromptDialog(mContext, R.style.PromptDialog, new PromptDialog.OnClickOKListener() {
                        @Override
                        public void onClickOK() {
                            batchHandleBtn.setText(R.string.batch_finish);
                            PrintHelper.getInstance().printBatchCups(OrderHelper.batchList);
                            PrintHelper.getInstance().printBatchBoxes(OrderHelper.batchList);
                        }
                    });
                    pd.setMode(PromptDialog.Mode.BOTH);
                    pd.setContent(content);
                    pd.show();
                } else if (mContext.getString(R.string.batch_finish).equals(batchBtnText)) {
                    //批量完成
                    String content = "是否将 " + OrderHelper.batchOrderCount + " 单同时完成？";
                    PromptDialog pd = new PromptDialog(mContext, R.style.PromptDialog, new PromptDialog.OnClickOKListener() {
                        @Override
                        public void onClickOK() {
                            batchHandleBtn.setText(R.string.batch_handle);
                            //开始循环请求
                            for (int i = 0; i < OrderHelper.batchList.size(); i++) {
                                HttpHelper.getInstance().reqFinishedProduce(OrderHelper.batchList.get(i).getId(), new JsonCallback<XlsResponse>() {
                                    @Override
                                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                                        handleFinishedProduceResponse(xlsResponse,call,response);
                                    }
                                });
                            }
                        }
                    });
                    pd.setMode(PromptDialog.Mode.BOTH);
                    pd.setContent(content);
                    pd.show();
                } else {

                }

            }
        });
        totalQuantityTxt = (TextView) contentView.findViewById(R.id.total_quantity);

        goodCommentText = (TextView) contentView.findViewById(R.id.tv_good_comment);
        badCommentText = (TextView) contentView.findViewById(R.id.tv_bad_comment);

        goodCommentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //好评列表
                Intent intent =  new Intent(mContext, CommentActivity.class);
                intent.putExtra("coment_type",OrderHelper.GOOD_COMMENT);
                mContext.startActivity(intent);
            }
        });

        badCommentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //差评列表
                Intent intent =  new Intent(mContext, CommentActivity.class);
                intent.putExtra("coment_type",OrderHelper.BAD_COMMENT);
                mContext.startActivity(intent);
            }
        });

    }

    private void initRadioGroupStatus(Context context){
        switch (LoginHelper.getLimitLevel(context)){
            case 1:
                radioGroup.check(R.id.rb_low);
                break;
            case 2:
                radioGroup.check(R.id.rb_middle);
                break;
            case 3:
                radioGroup.check(R.id.rb_all);
                break;
        }
    }


    //设置RecyclerView item之间的间距
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int spanCount;
        private int space;
        private boolean includeEdge;


        public SpaceItemDecoration(int spanCount, int space, boolean includeEdge) {
            this.spanCount = spanCount;
            this.space = space;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = space - column * space / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * space / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = space;
                }
                outRect.bottom = space; // item bottom
            } else {
                outRect.left = column * space / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = space - (column + 1) * space / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = space; // item top
                }
            }

        }
    }


    /**
     * 已完成列表总杯量
     * @param cupsAmount
     * @param ordersAmount
     */
    private void updateTotalAmount(int cupsAmount,int ordersAmount){
        totalQuantityTxt.setText("总杯量:" + cupsAmount);
        EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_FINISHED, ordersAmount));
    }

    private void requestData(Context context, int orderBy, int fillterInstant,boolean isRefresh,boolean isShowProgress){

        switch (subTabIndex){
            case TabList.TAB_TOPRODUCE:
                HttpHelper.getInstance().reqToProduceData(orderBy, fillterInstant,
                        LoginHelper.getLimitLevel(mContext),
                        new DialogCallback<XlsResponse>(getActivity()) {
                            @Override
                            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                                handleToProudceResponse(xlsResponse,call,response);
                            }
                        });
                break;
            case TabList.TAB_PRODUCING:
                HttpHelper.getInstance().reqProducingData(orderBy, fillterInstant, new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleProudcingResponse(xlsResponse,call,response);
                    }

                });
                break;
            case TabList.TAB_PRODUCED:
                HttpHelper.getInstance().reqProducedData(orderBy, fillterInstant, new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleProudcedResponse(xlsResponse,call,response);
                    }

                });
                break;
            case TabList.TAB_DELIVERING:
                HttpHelper.getInstance().reqDeliveryingData(orderBy, fillterInstant, new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleDeliveryingResponse(xlsResponse,call,response);
                    }

                });
                break;
            case TabList.TAB_FINISHED:
                HttpHelper.getInstance().reqFinishedTotalAmountData(new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleFinishedTotalAmountResponse(xlsResponse,call,response);
                    }

                });
                HttpHelper.getInstance().reqFinishedListData(orderBy, fillterInstant, 0, new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleFinishedResponse(xlsResponse, call, response);
                    }
                });
                break;
        }

        if(isRefresh){
            resetSpinners();
        }
    }
    private void initDetailView(View contentView){
        detailRootView = (LinearLayout) contentView.findViewById(R.id.detail_root_view);
        orderIdTxt = (TextView) contentView.findViewById(R.id.order_id);
        wholeOrderText = (TextView) contentView.findViewById(R.id.tv_whole_order_sn);
        orderTimeTxt = (TextView) contentView.findViewById(R.id.order_time);
        orderReportTxt = (TextView) contentView.findViewById(R.id.order_report);
        reachTimeTxt = (TextView) contentView.findViewById(R.id.reach_time);
        produceEffectTxt = (TextView) contentView.findViewById(R.id.produce_effect);
        receiveNameTxt  = (TextView) contentView.findViewById(R.id.receiver_name);
        receivePhoneTxt = (TextView) contentView.findViewById(R.id.receiver_phone);
        receiveAddressTxt = (TextView) contentView.findViewById(R.id.receiver_address);
        deliverInfoContainerLayout = (RelativeLayout) contentView.findViewById(R.id.deliver_info_container);
        deliverNameTxt = (TextView) contentView.findViewById(R.id.deliver_name);
        deliverPhoneTxt = (TextView) contentView.findViewById(R.id.deliver_phone);
        itemsContainerLayout = (LinearLayout) contentView.findViewById(R.id.items_container_layout);
        userRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_remark);
        userRemarkLayout.setOnClickListener(indoDetailListener);
        userRemarkTxt = (TextView) contentView.findViewById(R.id.user_remark);
        csadRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_csad_remark);
        csadRemarkLayout.setOnClickListener(indoDetailListener);
        csadRemarkTxt = (TextView) contentView.findViewById(R.id.csad_remark);
        userCommentLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_comment);
        userCommentLayout.setOnClickListener(indoDetailListener);
        userCommentTagsText = (TextView) contentView.findViewById(R.id.user_comment_tags);
        userCommentContentText = (TextView) contentView.findViewById(R.id.user_comment_content);
        twoBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_twobtn);
        oneBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_onebtn);
        produceAndPrintBtn = (Button) contentView.findViewById(R.id.btn_produce_print);
        finishProduceBtn = (Button) contentView.findViewById(R.id.btn_finish_produce);
        printOrderBtn = (Button) contentView.findViewById(R.id.btn_print_order);
        moreBtn  = (Button) contentView.findViewById(R.id.btn_more);
        prevBtn = (Button) contentView.findViewById(R.id.btn_prev);
        prevBtn.setOnClickListener(this);
        nextBtn = (Button) contentView.findViewById(R.id.btn_next);
        nextBtn.setOnClickListener(this);
    }
    private void updateDetailView(final OrderBean order){
        if(order==null){
            orderIdTxt.setText("");
            wholeOrderText.setText("");
            orderTimeTxt.setText("");
            orderReportTxt.setEnabled(false);
            reachTimeTxt.setText("");
            produceEffectTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            deliverNameTxt.setText("");
            deliverPhoneTxt.setText("");
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            userCommentTagsText.setText("");
            userCommentContentText.setText("");
            produceAndPrintBtn.setEnabled(false);
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
        }else{
            produceAndPrintBtn.setEnabled(true);
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);
            moreBtn.setEnabled(true);

            orderIdTxt.setText(OrderHelper.getShopOrderSn(order.getInstant(), order.getShopOrderNo()));
            wholeOrderText.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
            orderReportTxt.setEnabled(true);
            orderReportTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reportWindow == null) {
                        reportWindow = new ReportWindow(mContext);
                        reportWindow.setOrder(order);
                        reportWindow.showReportWindow(detailRootView);
                    } else {
                        reportWindow.setOrder(order);
                        reportWindow.showReportWindow(detailRootView);
                    }


                }
            });
            reachTimeTxt.setText(order.getInstant() == 1 ? "尽快送达" : OrderHelper.getDateToMonthDay(order.getExpectedTime()));

            if(order.getProduceStatus()==OrderStatus.UNPRODUCED){
                if(order.getInstant()==0){
                    produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                }else{
                    produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                }
                OrderHelper.showEffectOnly(order,produceEffectTxt);
            }else{
                OrderHelper.showEffect(order, finishProduceBtn, produceEffectTxt);
            }

            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            deliverNameTxt.setText(order.getCourierName());
            deliverPhoneTxt.setText(order.getCourierPhone());
            if(order.getStatus()== OrderStatus.UNASSIGNED){
                deliverInfoContainerLayout.setVisibility(View.GONE);
            }else {
                deliverInfoContainerLayout.setVisibility(View.VISIBLE);
            }
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            userCommentTagsText.setText(OrderHelper.getCommentTagsStr(order.getFeedbackTags()));
            userCommentContentText.setText(order.getFeedback());

            if(order.getProduceStatus() == OrderStatus.UNPRODUCED){
                twoBtnLayout.setVisibility(View.GONE);
                oneBtnLayout.setVisibility(View.VISIBLE);
                produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击开始生产（打印）按钮
                        EventBus.getDefault().post(new StartProduceEvent(order));
                    }
                });
            }else if(order.getProduceStatus() == OrderStatus.PRODUCING){
                twoBtnLayout.setVisibility(View.VISIBLE);
                oneBtnLayout.setVisibility(View.GONE);
                finishProduceBtn.setVisibility(View.VISIBLE);
                finishProduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //生产完成
                        EventBus.getDefault().post(new FinishProduceEvent(order));
                    }
                });
                if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                }else{
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new PrintOrderEvent(order));
                    }
                });
            }else{
                twoBtnLayout.setVisibility(View.VISIBLE);
                oneBtnLayout.setVisibility(View.GONE);
                finishProduceBtn.setVisibility(View.GONE);
                if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                }else{
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new PrintOrderEvent(order));
                    }
                });
            }


            moreBtn.setEnabled(true);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.inflate(R.menu.menu_order_detail_more);
                    if (order.isWxScan() && OrderStatus.PRODUCED == order.getProduceStatus()) {
                        popup.getMenu().findItem(R.id.menu_scan_code).setVisible(true);
                    } else {
                        popup.getMenu().findItem(R.id.menu_scan_code).setVisible(false);
                    }

                    if (order.getStatus() != OrderStatus.ASSIGNED) {
                        popup.getMenu().findItem(R.id.menu_undo_order).setVisible(false);
                    }
                    if (order.getStatus() != OrderStatus.UNASSIGNED) {
                        popup.getMenu().findItem(R.id.menu_assign_order).setVisible(false);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_undo_order:
                                    HttpHelper.getInstance().reqRecallOrder(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {

                                        @Override
                                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                                            handleRecallOrderResponse(xlsResponse,call,response);
                                        }
                                    });
                                    break;
                                case R.id.menu_scan_code:
                                    new ScanCodeQry(mContext, order).doRequest();
                                    break;
                                case R.id.menu_assign_order:
                                    Intent intent = new Intent(mContext, AssignOrderActivity.class);
                                    intent.putExtra("orderId", order.getId());
                                    mContext.startActivity(intent);
                                    break;
                            }
                            popup.dismiss();
                            return false;
                        }
                    });

                    popup.show();
                }
            });
        }

    }
    //填充item数据
    private void fillItemListData(LinearLayout ll,final OrderBean order){
        if(order==null){
            return;
        }
        ll.removeAllViews();
        List<ItemContentBean> items = order.getItems();
        for(ItemContentBean item:items){
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
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2,mContext);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2,mContext);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            String dingzhi = OrderHelper.getLabelStr(item.getRecipeFittingsList());
            if(!TextUtils.isEmpty(dingzhi)){
                TextView tv5 = new TextView(mContext);
                tv5.setId(R.id.item_flag);
                tv5.setText(dingzhi);
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0, 1, OrderHelper.dip2Px(14, mContext), OrderHelper.dip2Px(14, mContext));
                tv5.setCompoundDrawablePadding(OrderHelper.dip2Px(4, mContext));
                tv5.setCompoundDrawables(drawable, null, null, null);
                tv5.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
                tv5.setTextColor(mContext.getResources().getColor(R.color.font_black));
                RelativeLayout.LayoutParams lp5=new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp5.addRule(RelativeLayout.BELOW,tv1.getId());
                lp5.addRule(RelativeLayout.ALIGN_LEFT,tv1.getId());
                tv5.setLayoutParams(lp5);
                rl.addView(tv5);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2,mContext);
            ll.addView(rl,lp);
        }
        if(!TextUtils.isEmpty(order.getWishes())){
            TextView tv3 = new TextView(mContext);
            tv3.setText("礼品卡");
            tv3.setMaxEms(9);
            tv3.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv3.setTextColor(mContext.getResources().getColor(R.color.font_black));
            TextView tv4 = new TextView(mContext);
            tv4.setText(order.getWishes());
            tv4.getPaint().setFakeBoldText(true);
            tv4.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            RelativeLayout r2 = new RelativeLayout(mContext);
            r2.setBackgroundColor(Color.YELLOW);
            RelativeLayout.LayoutParams lp3=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp3.leftMargin = OrderHelper.dip2Px(2,mContext);
            tv3.setLayoutParams(lp3);
            r2.addView(tv3);

            RelativeLayout.LayoutParams lp4=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp4.rightMargin = OrderHelper.dip2Px(2,mContext);
            tv4.setLayoutParams(lp4);
            r2.addView(tv4);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4,mContext);
            ll.addView(r2, lp);
        }

        TextView tv6 = new TextView(mContext);
        tv6.setText(mContext.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        tv6.setGravity(Gravity.CENTER);
        tv6.getPaint().setFakeBoldText(true);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp6.topMargin = OrderHelper.dip2Px(30,mContext);
        tv6.setLayoutParams(lp6);
        ll.addView(tv6, lp6);

        ll.invalidate();
    }
    private void initTabButtons(View contentView){
        toDoTab = (ListTabButton) contentView.findViewById(R.id.tab_to_do);
        doingTab  = (ListTabButton) contentView.findViewById(R.id.tab_doing);
        haveDoneTab = (ListTabButton) contentView.findViewById(R.id.tab_have_done);
        deliveringTab = (ListTabButton) contentView.findViewById(R.id.tab_delivering);
        deliveryFinishedTab = (ListTabButton) contentView.findViewById(R.id.tab_delivery_finished);

        toDoTab.setClickBg(true);
        doingTab.setClickBg(false);
        haveDoneTab.setClickBg(false);
        deliveringTab.setClickBg(false);
        deliveryFinishedTab.setClickBg(false);

        toDoTab.setOnClickListener(ltbListener);
        doingTab.setOnClickListener(ltbListener);
        haveDoneTab.setOnClickListener(ltbListener);
        deliveringTab.setOnClickListener(ltbListener);
        deliveryFinishedTab.setOnClickListener(ltbListener);
    }

    private void resetSpinners(){
        if(!LoginHelper.isSFMode()){
            orderBy = OrderHelper.PRODUCE_TIME;
            fillterInstant = OrderHelper.ALL;
            sortSpinner.setSelection(0, true);
            categorySpinner.setSelection(0, true);
        }

    }
    private void initSpinner(View contentView,Context context){
        sortSpinner = (Spinner) contentView.findViewById(R.id.spinner_sort);
        categorySpinner = (Spinner) contentView.findViewById(R.id.spinner_category);

        final ArrayAdapter< String> adapter_sort = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_sort.add(context.getResources().getString(R.string.sort_by_produce_effect));
        adapter_sort.add(context.getResources().getString(R.string.sort_by_order_time));
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter_sort);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        orderBy = OrderHelper.PRODUCE_TIME;
                        break;
                    case 1:
                        orderBy = OrderHelper.ORDER_TIME;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter< String> adapter_category = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_category.add(context.getResources().getString(R.string.category_all));
        adapter_category.add(context.getResources().getString(R.string.category_now));
        adapter_category.add(context.getResources().getString(R.string.category_order));
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter_category);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        fillterInstant = OrderHelper.ALL;
                        break;
                    case 1:
                        fillterInstant = OrderHelper.INSTANT;
                        break;
                    case 2:
                        fillterInstant = OrderHelper.APPOINTMENT;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(LoginHelper.isSFMode()){
            sortSpinner.setVisibility(View.INVISIBLE);
            categorySpinner.setVisibility(View.INVISIBLE);
        }else {
            sortSpinner.setVisibility(View.VISIBLE);
            categorySpinner.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void OnUpdatePrintStatusEvent(UpdatePrintStatusEvent event){
        Log.d(TAG, "OnUpdatePrintStatusEvent,orderSn = " + event.orderSn);
        //打印界面退出的时候，刷新一下打印按钮文字
        if(LoginHelper.isSFMode()){
            sfAdaper.notifyDataSetChanged();
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 订单被撤销
     * @param event
     */
    @Subscribe
    public void onCancelOrderEvent(CancelOrderEvent event){
        Log.d(TAG, "event:" + event.orderId);
    }

    @Subscribe
    public void onClickCommentEvent(ClickCommentEvent event){
        updateDetailView(event.orderBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentCountEvent(CommentCountEvent event){
        HttpHelper.getInstance().reqCommentCount(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleCommentCountResponse(xlsResponse,call,response);
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        subTabIndex = TabList.TAB_TOPRODUCE;
        requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onClick(View v) {
        if(LoginHelper.isSFMode()){
            return;
        }
        switch (v.getId()){
            case R.id.btn_prev:  //上一单
                if(adapter.selected>0){
                    adapter.selected -= 1;
                    adapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(adapter.list.get(adapter.selected)));
                    ordersGridView.getRecyclerView().smoothScrollToPosition(adapter.selected);

                }
                break;
            case R.id.btn_next:  //下一单
                if(adapter.selected<adapter.list.size()-1 && adapter.selected!=-1 ){
                    adapter.selected += 1;
                    adapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(adapter.list.get(adapter.selected)));
                    ordersGridView.getRecyclerView().smoothScrollToPosition(adapter.selected);
                }
                break;
        }

    }

    //隐藏spinner和刷新按钮
    private void showWidget(boolean isShow){
        if(isShow){
            if(!LoginHelper.isSFMode()){
                sortSpinner.setVisibility(View.VISIBLE);
                categorySpinner.setVisibility(View.VISIBLE);
            }
            radioGroup.setVisibility(View.VISIBLE);
            refreshbtn.setVisibility(View.VISIBLE);
            batchHandleBtn.setVisibility(View.INVISIBLE);
            if(batchHandleBtn.getText().toString().equals(mContext.getString(R.string.batch_handle))){
                batchPromptText.setVisibility(View.GONE);
            }else{
                batchPromptText.setVisibility(View.VISIBLE);
            }
        }else{
            sortSpinner.setVisibility(View.INVISIBLE);
            categorySpinner.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            refreshbtn.setVisibility(View.INVISIBLE);
            batchHandleBtn.setVisibility(View.INVISIBLE);
            batchPromptText.setVisibility(View.GONE);
        }
    }



    class ListTabButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tab_to_do:
                    toDoTab.setClickBg(true);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(true);
                    subTabIndex = TabList.TAB_TOPRODUCE;
                    requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
                    resetSpinners();
                    ordersGridView.setPushRefreshEnable(false);
                    break;
                case R.id.tab_doing:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(true);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(true);
                    subTabIndex = TabList.TAB_PRODUCING;
                    requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
                    resetSpinners();
                    ordersGridView.setPushRefreshEnable(false);
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = TabList.TAB_PRODUCED;
                    requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
                    resetSpinners();
                    ordersGridView.setPushRefreshEnable(false);
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = TabList.TAB_DELIVERING;
                    requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
                    resetSpinners();
                    ordersGridView.setPushRefreshEnable(false);
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    showWidget(false);
                    subTabIndex = TabList.TAB_FINISHED;
                    requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, true);
                    resetSpinners();
                    ordersGridView.setPushRefreshEnable(true);
                    break;
            }
            if(subTabIndex ==  TabList.TAB_FINISHED){
                totalQuantityTxt.setVisibility(View.VISIBLE);
            }else{
                totalQuantityTxt.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 刷新列表的时候，更新当前列表标签上的订单数量
     * @param event
     */
    @Subscribe
    public void onUpdateTabOrderListCountEvent(UpdateTabOrderListCountEvent event){
        switch (event.witchTab){
            case TabList.TAB_TOPRODUCE:
                toDoTab.setCount(event.count);
                break;
            case TabList.TAB_PRODUCING:
                doingTab.setCount(event.count);
                break;
            case TabList.TAB_PRODUCED:
                haveDoneTab.setCount(event.count);
                break;
            case TabList.TAB_DELIVERING:
                deliveringTab.setCount(event.count);
                break;
            case TabList.TAB_FINISHED:
                deliveryFinishedTab.setCount(event.count);
                break;
        }
    }

    /**
     * 对订单操作后更改相关列表中的订单数量角标显示
     * @param event
     */
    @Subscribe
    public void onChangeTabCountByActionEvent(ChangeTabCountByActionEvent event){
        if(LoginHelper.isSFMode()){
            switch (event.action){
                case OrderAction.STARTPRODUCE:
                    toDoTab.setCount(toDoTab.getCount()-event.count);
                    doingTab.setCount(doingTab.getCount()+event.count);
                    break;
                case OrderAction.FINISHPRODUCE:
                    doingTab.setCount(doingTab.getCount()-event.count);
                    haveDoneTab.setCount(haveDoneTab.getCount()+event.count);
                    break;
                case OrderAction.SCANCODE:
                    haveDoneTab.setCount(haveDoneTab.getCount()-event.count);
                    deliveryFinishedTab.setCount(deliveryFinishedTab.getCount()+event.count);
                    break;
            }
        }else{
            switch (event.action){
                case OrderAction.STARTPRODUCE:
                    toDoTab.setCount(toDoTab.getCount()-1);
                    doingTab.setCount(doingTab.getCount()+1);
                    break;
                case OrderAction.FINISHPRODUCE:
                    doingTab.setCount(doingTab.getCount()-1);
                    haveDoneTab.setCount(haveDoneTab.getCount()+1);
                    break;
                case OrderAction.SCANCODE:
                    haveDoneTab.setCount(haveDoneTab.getCount()-1);
                    deliveryFinishedTab.setCount(deliveryFinishedTab.getCount()+1);
                    break;
            }
        }

    }


    /**
     * 提交问题订单后立即更新订单显示状态
     * @param event
     */
    @Subscribe
    public void onCommitIssueOrderEvent(CommitIssueOrderEvent event){
        long orderId = event.orderId;
        for(int i=0;i<adapter.list.size();i++){
            if(adapter.list.get(i).getId()==orderId){
                adapter.list.get(i).setIssueOrder(true);
                adapter.notifyDataSetChanged();
                EventBus.getDefault().post(new UpdateOrderDetailEvent(adapter.list.get(i)));
                break;
            }
        }

    }

    /**
     * 点击开始生产按钮事件
     * @param event
     */
    @Subscribe
    public void onStartProduceEvent(StartProduceEvent event){
        startProduceAndPrint(mContext, event.order);
    }

    /**
     * 点击生产完成的按钮事件
     * @param event
     */
    @Subscribe
    public void onFinishProduceEvent(FinishProduceEvent event){
        finishProduce(mContext, event.order);
    }

    /**
     * 单独点击打印按钮事件
     * @param event
     */
    @Subscribe
    public void onPrintOrderEvent(PrintOrderEvent event){
        printOrder(mContext, event.order);
    }


    @Subscribe
    public void onUpdateOrderDetailEvent(UpdateOrderDetailEvent event){
        updateDetailView(event.orderBean);
    }


    //新订单消息触发事件
    @Subscribe
    public void onNewOrderComing(NewOderComingEvent event){
        Log.d(TAG,"新订单消息触发事件");
        Log.d(TAG,"requestData ---- onNewOrderComing");
        requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, false);
    }





    //生产完成后更新批量订单提示栏的可见状态
    public void updateBatchPromptTextView(int leftBatchOrderNum){
        if(leftBatchOrderNum<=0){
            batchPromptText.setVisibility(View.GONE);
            batchHandleBtn.setText(R.string.batch_handle);
        }else{
            batchPromptText.setVisibility(View.VISIBLE);
        }
    }


    //处理服务器返回数据---待生产
    private void handleToProudceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(subTabIndex!=TabList.TAB_TOPRODUCE){
            return;
        }
        if(xlsResponse.status==0){
            if(LoginHelper.isSFMode()){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(getActivity(), xlsResponse);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_TOPRODUCE, OrderHelper.getGroupTotalCount(sfGroupBeans)));
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_TOPRODUCE, orderBeans.size()));
                adapter.setData(orderBeans);
                //检查列表中是否有未完成的合并订单
                if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                    OrderHelper.batchList.clear();
                    updateBatchPromptTextView(0);
                }
            }
        }else if(xlsResponse.status==103){
            //token 无效
            ToastUtil.showToast(mContext, xlsResponse.message);
            LoginBean loginBean = LoginHelper.getLoginBean(mContext);
            loginBean.setToken("");
            LoginHelper.saveLoginBean(mContext, loginBean);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            Activity activity = getActivity();
            if(activity!=null){
                activity.finish();
            }

        }

    }

    //处理服务器返回数据---生产中
    private void handleProudcingResponse(XlsResponse xlsResponse,Call call,Response response){
        if(LoginHelper.isSFMode()){
            List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(getActivity(), xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCING,OrderHelper.getGroupTotalCount(sfGroupBeans)));
            sfAdaper.setData(sfGroupBeans);
        }else{
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCING,orderBeans.size()));
            adapter.setData(orderBeans);
            //检查列表中是否有未完成的合并订单
            if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                OrderHelper.batchList.clear();
                updateBatchPromptTextView(0);
            }
        }
    }

    //处理服务器返回数据---已生产
    private void handleProudcedResponse(XlsResponse xlsResponse,Call call,Response response){
        if(LoginHelper.isSFMode()){
            List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(getActivity(),xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,OrderHelper.getGroupTotalCount(sfGroupBeans)));
            sfAdaper.setData(sfGroupBeans);
        }else{
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,orderBeans.size()));
            adapter.setData(orderBeans);
        }
    }

    //处理服务器返回数据---配送中
    private void handleDeliveryingResponse(XlsResponse xlsResponse,Call call,Response response){
        if(LoginHelper.isSFMode()){
            List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(getActivity(),xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_DELIVERING,OrderHelper.getGroupTotalCount(sfGroupBeans)));
            sfAdaper.setData(sfGroupBeans);
        }else{
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_DELIVERING,orderBeans.size()));
            adapter.setData(orderBeans);
        }
    }

    //处理服务器返回数据---已完成
    private void handleFinishedResponse(XlsResponse xlsResponse,Call call,Response response){
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        if(LoginHelper.isSFMode()){
            List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(getActivity(), xlsResponse);
            if("yes".equalsIgnoreCase(isLoadMore)){
                sfAdaper.addData(sfGroupBeans);
            }else{
                sfAdaper.setData(sfGroupBeans);
            }
            if(sfAdaper.groupList.size()>0){
                finishedLastOrderId = sfAdaper.groupList.get(sfAdaper.groupList.size() - 1).getId();
            }else{
                finishedLastOrderId = 0;
            }
        }else{
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
            if("yes".equalsIgnoreCase(isLoadMore)){
                adapter.addData(orderBeans);
            }else{
                adapter.setData(orderBeans);
            }

            if(adapter.list.size()>0){
                finishedLastOrderId = adapter.list.get(adapter.list.size()-1).getId();
            }else {
                finishedLastOrderId = 0;
            }

        }
    }

    //处理服务器返回的已完成订单总单量和杯量
    private void handleFinishedTotalAmountResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            int ordersAmount = xlsResponse.data.getIntValue("totalOrdersAmount");
            int cupsAmount = xlsResponse.data.getIntValue("totalCupsAmount");
            updateTotalAmount(cupsAmount,ordersAmount);
        }
    }

    //处理服务器返回数据---订单收回
    private void handleRecallOrderResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            if(LoginHelper.isSFMode()){
                for(SFGroupBean sfGroup:sfAdaper.groupList){
                    if(id==sfGroup.getId()){
                        for(OrderBean orderBean:sfGroup.getItemGroup()){
                            orderBean.setStatus(OrderStatus.UNASSIGNED);
                        }
                        break;
                    }
                }
                sfAdaper.notifyDataSetChanged();
            }else{
                for(OrderBean order:adapter.list){
                    if(id == order.getId()){
                        order.setStatus(OrderStatus.UNASSIGNED);
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new UpdateOrderDetailEvent(order));
                        break;
                    }
                }
            }

        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }

    /**
     * 处理开始生产结果
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleStartProduceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status == 0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            if(LoginHelper.isSFMode()){
                sfAdaper.changeAndRemoveOrderFromList(id, OrderStatus.PRODUCING);
            }else{
                adapter.removeOrderFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1));
            }
        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }

    /**
     * 处理生产完成动作返回的结果
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleFinishedProduceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status == 0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            if(LoginHelper.isSFMode()){
                sfAdaper.changeAndRemoveOrderFromList(id,OrderStatus.PRODUCED);
            }else{
                adapter.removeOrderFromList(id);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));
            }
        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }

    /**
     * 处理评论数量接口返回的数据
     */
    private void handleCommentCountResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            int positive = xlsResponse.data.getIntValue("positive");
            int negative = xlsResponse.data.getIntValue("negative");
            updateCommentCount(positive,negative);
        }
    }






    //扫码交付接口
    class ScanCodeQry implements Qry{
        private Context context;
        private OrderBean mOrder;

        public ScanCodeQry(Context context, OrderBean mOrder) {
            this.context = context;
            this.mOrder = mOrder;
        }

        @Override
        public void doRequest() {
            LoginBean loginBean = LoginHelper.getLoginBean(context);
            String token = loginBean.getToken();
            int shopId = loginBean.getShopId();
            String url = Urls.BASE_URL+shopId+"/order/"+mOrder.getId()+"/deliver?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"ScanCodeQry:resp ="+resp);
            if(resp == null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(resp.status==0){
                ToastUtil.showToast(context, R.string.do_success);
                if(LoginHelper.isSFMode()){
                    sfAdaper.changeAndRemoveOrderFromList(mOrder.getId(),OrderStatus.FINISHED);
                }else{
                    adapter.removeOrderFromList(mOrder.getId());
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.SCANCODE,1));
                }

            }
        }
    }


    private void updateCommentCount(int positive,int negative){
        goodCommentText.setText("好评"+positive);
        badCommentText.setText("差评"+negative);
    }


    class RadioButtonClicklistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rb_low:
                    LoginHelper.saveLimitLevel(mContext,1);
                    break;
                case R.id.rb_middle:
                    LoginHelper.saveLimitLevel(mContext,2);
                    break;
                case R.id.rb_all:
                    LoginHelper.saveLimitLevel(mContext,3);
                    break;
            }
        }
    }


    class IndoDetailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_user_remark:
                    //用户备注
                    InfoDetailDialog.getInstance(mContext).show(userRemarkTxt.getText().toString());
                    break;
                case R.id.ll_csad_remark:
                    //客服备注
                    InfoDetailDialog.getInstance(mContext).show(csadRemarkTxt.getText().toString());
                    break;
                case R.id.ll_user_comment:
                    //用户评价
                    InfoDetailDialog.getInstance(mContext).show(userCommentTagsText.getText().toString()+"\n"+userCommentContentText.getText().toString());
                    break;
            }
        }
    }


    //点击开始生产并打印
    public void startProduceAndPrint(final Context context,final OrderBean order){
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                Log.d(TAG, "orderId = " + order.getOrderSn());
                //请求服务器改变该订单状态，由 待生产--生产中
                HttpHelper.getInstance().reqStartProduce(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleStartProduceResponse(xlsResponse,call,response);
                    }
                });
                //打印全部
                PrintHelper.getInstance().printOrderInfo(order);
                PrintHelper.getInstance().printOrderItems(order);
            }
        });
        grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
        grabConfirmDialog.show();

    }

    //点击生产完成
    public void finishProduce(final Context context,final OrderBean order){
        ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
            @Override
            public void onClickYes() {
                Log.d(TAG, "orderId = " + order.getOrderSn());
                HttpHelper.getInstance().reqFinishedProduce(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleFinishedProduceResponse(xlsResponse,call,response);
                    }
                });
            }
        });
        grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
            grabConfirmDialog.show();
    }

    //点击打印
    public void printOrder(Context context,final OrderBean order){
        //打印按钮
        if (order.getInstant() == 0) {
            Intent intent = new Intent(context, PrintOrderActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        } else {
            //及时单
            Intent intent = new Intent(context, PrintOrderActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        }

    }


}
