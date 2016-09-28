package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.AssignOrderActivity;
import com.lyancafe.coffeeshop.activity.CommentActivity;
import com.lyancafe.coffeeshop.activity.LocationActivity;
import com.lyancafe.coffeeshop.activity.PrintOrderActivity;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.adapter.OrderListViewAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.CancelOrderEvent;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.ClickCommentEvent;
import com.lyancafe.coffeeshop.event.CommentCountEvent;
import com.lyancafe.coffeeshop.event.CommitIssueOrderEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdatePrintStatusEvent;
import com.lyancafe.coffeeshop.event.UpdateTabOrderListCountEvent;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.service.AutoFetchOrdersService;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.DeliverImageDialog;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.ListTabButton;
import com.lyancafe.coffeeshop.widget.PromptDialog;
import com.lyancafe.coffeeshop.widget.ReportWindow;
import com.lyancafe.coffeeshop.widget.SimpleConfirmDialog;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment implements View.OnClickListener{

    private static final String TAG  ="OrdersFragment";
    private View mContentView;
    private Activity mContext;
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

    private RecyclerView ordersGridView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mSFLayoutManager;
    private OrderGridViewAdapter adapter;
    private OrderListViewAdapter sfAdaper;

    private TextView refreshbtn;
    private TextView batchHandleBtn;
    private TextView batchPromptText;
    private TextView totalQuantityTxt;
    private LinearLayout commentLayout;
    private TextView goodCommentText;
    private TextView badCommentText;

    private int orderBy = 0;
    private int fillterInstant = 0;

    private ReportWindow reportWindow;
    NotificationManager mNotificationManager;

    private IndoDetailListener indoDetailListener;


    //详情板块当前显示的订单id
    private long mOrderId;

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
    private LinearLayout  deliverLayout;
    private TextView deliverNameTxt;
    private TextView deliverPhoneTxt;
    private TextView deliverLocationInfoTxt;
    private LinearLayout itemsContainerLayout;
//    private TextView orderPriceTxt;
//    private TextView payWayTxt;
//    private TextView moneyTxt;
    private LinearLayout userRemarkLayout;
    private TextView userRemarkTxt;
    private LinearLayout csadRemarkLayout;
    private TextView csadRemarkTxt;
    private LinearLayout userCommentLayout;
//    private TextView userCommentTagsText;
//    private TextView userCommentContentText;
    private Button checkCommentBtn;
    private LinearLayout twoBtnLayout;
    private LinearLayout oneBtnLayout;
    private Button produceAndPrintBtn;
    private Button finishProduceBtn;
    private Button printOrderBtn;
    private Button moreBtn;
    private Button prevBtn;
    private Button nextBtn;

    private OrdersReceiver ordersReceiver;
    private Handler mHandler = new Handler(){
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG, "onAttach");
        registerReceiver(mContext);
    }

    private void registerReceiver(Context context){
        ordersReceiver = new OrdersReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoFetchOrdersService.ACTION_REFRESH_ORDERS);
        context.registerReceiver(ordersReceiver, filter);
    }

    private void unRegisterReceiver(Context context){
        if(ordersReceiver!=null){
            context.unregisterReceiver(ordersReceiver);
            ordersReceiver = null;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ltbListener = new ListTabButtonListener();
        indoDetailListener = new IndoDetailListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(mContentView);
        EventBus.getDefault().register(this);
        return mContentView;
    }

    private void initViews(View contentView){
        ordersGridView = (RecyclerView) contentView.findViewById(R.id.gv_order_list);
        if(LoginHelper.isSFMode()){
            mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
            sfAdaper = new OrderListViewAdapter(mContext);
            ordersGridView.setAdapter(sfAdaper);
        }else{
            mLayoutManager = new GridLayoutManager(mContext,4,GridLayoutManager.VERTICAL,false);
            adapter = new OrderGridViewAdapter(mContext,OrdersFragment.this);
            ordersGridView.setAdapter(adapter);
        }

        ordersGridView.setLayoutManager(mLayoutManager);
        ordersGridView.setHasFixedSize(true);
        ordersGridView.setItemAnimator(new DefaultItemAnimator());
        ordersGridView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(16, mContext), false));


        initTabButtons(contentView);
        initDetailView(contentView);
        initSpinner(contentView, mContext);

        shopNameText = (TextView) contentView.findViewById(R.id.tv_shop_name);
        shopNameText.setText(LoginHelper.getShopName(mContext));

        batchPromptText = (TextView) contentView.findViewById(R.id.tv_batch_prompt);
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
                                new DoFinishProduceQry(mContext,OrderHelper.batchList.get(i), false).doRequest();
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

        commentLayout = (LinearLayout) contentView.findViewById(R.id.ll_comment);
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

   /* *//**
     * 列表数据变更，及时更新详情
     *//*
    private void UpdateUI(){
        if(LoginHelper.isSFMode()){
            sfAdaper.notifyDataSetChanged();
        }else{
            adapter.notifyDataSetChanged();
        }
        EventBus.getDefault().post(new UpdateOrderDetailEvent());
    }*/

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

    //更新总杯量
    private void updateTotalQuantity(List<OrderBean> orderList){
        if(orderList==null){
            return;
        }
        int sum = 0;
        for(int i=0;i<orderList.size();i++){
            int item_num = 0;
            List<ItemContentBean> items = orderList.get(i).getItems();
            for(int j=0;j<items.size();j++){
                item_num+=items.get(j).getQuantity();
            }
            sum+=item_num;
        }

        totalQuantityTxt.setText("总杯量:" + sum);
    }
    //更新顺风单总杯量
    private void updateSFTotalQuantity(List<SFGroupBean> sfGroupBeanList){
        totalQuantityTxt.setText("总杯量:" + OrderHelper.getSFOrderTotalQutity(sfGroupBeanList));
    }
    private void requestData(Context context, int orderBy, int fillterInstant,boolean isRefresh,boolean isShowProgress){

        switch (subTabIndex){
            case TabList.TAB_TOPRODUCE:
                new OrderToProduceQry(context, orderBy, fillterInstant,isShowProgress,LoginHelper.isSFMode()).doRequest();
                break;
            case TabList.TAB_PRODUCING:
                new OrderProducingQry(context, orderBy, fillterInstant,isShowProgress,LoginHelper.isSFMode()).doRequest();
                break;
            case TabList.TAB_PRODUCED:
                new OrderProducedQry(context, orderBy, fillterInstant,isShowProgress,LoginHelper.isSFMode()).doRequest();
                break;
            case TabList.TAB_DELIVERING:
                new OrderDeliveryingQry(context, orderBy, fillterInstant,isShowProgress,LoginHelper.isSFMode()).doRequest();
                break;
            case TabList.TAB_FINISHED:
                new OrderFinishedQry(context, orderBy, fillterInstant,isShowProgress,LoginHelper.isSFMode()).doRequest();
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
        deliverLayout = (LinearLayout) contentView.findViewById(R.id.ll_deliver_layout);
        deliverNameTxt = (TextView) contentView.findViewById(R.id.deliver_name);
        deliverPhoneTxt = (TextView) contentView.findViewById(R.id.deliver_phone);
        deliverLocationInfoTxt = (TextView) contentView.findViewById(R.id.deliver_location_info);
        itemsContainerLayout = (LinearLayout) contentView.findViewById(R.id.items_container_layout);
//        orderPriceTxt = (TextView) contentView.findViewById(R.id.order_price);
//        payWayTxt = (TextView) contentView.findViewById(R.id.pay_way);
//        moneyTxt = (TextView) contentView.findViewById(R.id.money);
        userRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_remark);
        userRemarkLayout.setOnClickListener(indoDetailListener);
        userRemarkTxt = (TextView) contentView.findViewById(R.id.user_remark);
        csadRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_csad_remark);
        csadRemarkLayout.setOnClickListener(indoDetailListener);
        csadRemarkTxt = (TextView) contentView.findViewById(R.id.csad_remark);
        userCommentLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_comment);
        checkCommentBtn = (Button) contentView.findViewById(R.id.btn_check_comments);
        checkCommentBtn.setOnClickListener(indoDetailListener);
    //    userCommentTagsText = (TextView) contentView.findViewById(R.id.user_comment_tags);
    //    userCommentContentText = (TextView) contentView.findViewById(R.id.user_comment_content);
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
            deliverLocationInfoTxt.setVisibility(View.GONE);
            fillItemListData(itemsContainerLayout, order);
//            orderPriceTxt.setText("");
//            payWayTxt.setText("");
//            moneyTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
       //     userCommentTagsText.setText("");
       //     userCommentContentText.setText("");
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
            checkCommentBtn.setEnabled(false);
        }else{
            mOrderId = order.getId();
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);
            moreBtn.setEnabled(true);
            checkCommentBtn.setEnabled(true);

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
        //    deliverLocationInfoTxt.setVisibility(View.VISIBLE);
            deliverLocationInfoTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //启动地图Activity
                    Intent intent = new Intent(mContext, LocationActivity.class);
                    mContext.startActivity(intent);
                }
            });
            if(order.getStatus()== OrderStatus.UNASSIGNED){
                deliverInfoContainerLayout.setVisibility(View.GONE);
            }else {
                deliverInfoContainerLayout.setVisibility(View.VISIBLE);
            }
            deliverLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出对话框显示头像
                    new DeliverImageDialog(mContext, R.style.PromptDialog, order.getCourierName(), order.getCourierPhone(), order.getCourierImgUrl()).show();

                }
            });
            fillItemListData(itemsContainerLayout, order);
//            orderPriceTxt.setText("应付: " + OrderHelper.getMoneyStr(OrderHelper.getTotalPrice(order)));
//            payWayTxt.setText(order.getPayChannelStr());
//            moneyTxt.setText(OrderHelper.getMoneyStr(order.getPaid()));
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
        //    userCommentTagsText.setText(OrderHelper.getCommentTagsStr(order.getFeedbackTags()));
        //    userCommentContentText.setText(order.getFeedback());

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
                                    new RecallQry(mContext, order).doRequest();
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
                Drawable drawable = ContextCompat.getDrawable(CoffeeShopApplication.getInstance(),R.mipmap.flag_ding);
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
        sortSpinner.setSelection(0, true);
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
                requestData(mContext, orderBy, fillterInstant, false, true);
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
        categorySpinner.setSelection(0, true);
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
                requestData(mContext, orderBy, fillterInstant, false,true);
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
        EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
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
        new CommentCountQry(mContext).doRequest();
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
        if(adapter!=null && adapter.timer!=null){
            adapter.timer.cancel();
            adapter.timer.purge();
        }
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
        unRegisterReceiver(mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_prev:  //上一单
                if(adapter.selected>0){
                    adapter.selected -= 1;
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
                    ordersGridView.smoothScrollToPosition(adapter.selected);

                }
                break;
            case R.id.btn_next:  //下一单
                if(adapter.selected<adapter.list.size()-1 && adapter.selected!=-1 ){
                    adapter.selected += 1;
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
                    ordersGridView.smoothScrollToPosition(adapter.selected);
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
                    new OrderToProduceQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true,LoginHelper.isSFMode()).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_doing:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(true);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(true);
                    subTabIndex = TabList.TAB_PRODUCING;
                    new OrderProducingQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true,LoginHelper.isSFMode()).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = TabList.TAB_PRODUCED;
                    new OrderProducedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true,LoginHelper.isSFMode()).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = TabList.TAB_DELIVERING;
                    new OrderDeliveryingQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true,LoginHelper.isSFMode()).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    showWidget(false);
                    subTabIndex = TabList.TAB_FINISHED;
                    new OrderFinishedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true,LoginHelper.isSFMode()).doRequest();
                    resetSpinners();
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
                break;
            }
        }
        EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
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
        if(LoginHelper.isSFMode()){
            if(sfAdaper!=null){
                sfAdaper.notifyDataSetChanged();
            }
        }else{
            if(adapter!=null){
                adapter.notifyDataSetChanged();
                if(event.orderBean==null){
                    event.orderBean = adapter.list.get(adapter.selected);
                }
            }
        }
        updateDetailView(event.orderBean);
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

    //待生产订单列表接口
    class OrderToProduceQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        private boolean isSFMode;

        public OrderToProduceQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress,boolean isSFMode) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
            this.isSFMode = isSFMode;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = null;
            if(isSFMode){
                url = HttpUtils.BASE_URL+shopId+"/orders/today/toproduce/tailwind?token="+token;
            }else{
                url = HttpUtils.BASE_URL+shopId+"/orders/today/toproduce?token="+token;
            }
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderToProduceQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(subTabIndex!=TabList.TAB_TOPRODUCE){
                return;
            }
            if(isSFMode){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_TOPRODUCE, OrderHelper.getGroupTotalCount(sfGroupBeans)));
                if(sfGroupBeans.size()>sfAdaper.groupList.size() && !isShowProgress){
                    sendNotificationForAutoNewOrders(true);
                }
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_TOPRODUCE, orderBeans.size()));
                if(orderBeans.size()>adapter.cacheToProduceList.size() && !isShowProgress){
                    sendNotificationForAutoNewOrders(true);
                }
                adapter.setData(orderBeans);
                //检查列表中是否有未完成的合并订单
                if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                    OrderHelper.batchList.clear();
                    updateBatchPromptTextView(0);
                }
            }


        }
    }

    //生产中列表接口
    class OrderProducingQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        private boolean isSFMode;

        public OrderProducingQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress,boolean isSFMode) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
            this.isSFMode = isSFMode;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = null;
            if(isSFMode){
                url = HttpUtils.BASE_URL+shopId+"/orders/today/producing/tailwind?token="+token;
            }else{
                url = HttpUtils.BASE_URL+shopId+"/orders/today/producing?token="+token;
            }
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderProducingQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(isSFMode){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCING,OrderHelper.getGroupTotalCount(sfGroupBeans)));
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCING,orderBeans.size()));
                adapter.setData(orderBeans);
                //检查列表中是否有未完成的合并订单
                if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                    OrderHelper.batchList.clear();
                    updateBatchPromptTextView(0);
                }
            }


        }
    }


    //已生产订单列表接口
    class OrderProducedQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        private boolean isSFMode;

        public OrderProducedQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress,boolean isSFMode) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
            this.isSFMode = isSFMode;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = null;
            if(isSFMode){
                url = HttpUtils.BASE_URL+shopId+"/orders/today/produced/tailwind?token="+token;
            }else{
                url = HttpUtils.BASE_URL+shopId+"/orders/today/produced?token="+token;
            }
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderProducedQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(isSFMode){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(context,resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,OrderHelper.getGroupTotalCount(sfGroupBeans)));
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,orderBeans.size()));
                adapter.setData(orderBeans);
            }

        }
    }

    //配送中订单列表接口
    class OrderDeliveryingQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        private boolean isSFMode;

        public OrderDeliveryingQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress,boolean isSFMode) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
            this.isSFMode = isSFMode;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = null;
            if(isSFMode){
                url = HttpUtils.BASE_URL+shopId+"/orders/today/delivering/tailwind?token="+token;
            }else{
                url = HttpUtils.BASE_URL+shopId+"/orders/today/delivering?token="+token;
            }

            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderDeliveryingQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(isSFMode){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(context,resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_DELIVERING,OrderHelper.getGroupTotalCount(sfGroupBeans)));
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_DELIVERING,orderBeans.size()));
                adapter.setData(orderBeans);
            }

        }
    }

    //已完成订单列表接口
    class OrderFinishedQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        private boolean isSFMode;

        public OrderFinishedQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress,boolean isSFMode) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
            this.isSFMode = isSFMode;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = null;
            if(isSFMode){
                url = HttpUtils.BASE_URL+shopId+"/orders/today/finished/tailwind?token="+token;
            }else{
                url = HttpUtils.BASE_URL+shopId+"/orders/today/finished?token="+token;
            }
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderFinishedQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(isSFMode){
                List<SFGroupBean> sfGroupBeans = SFGroupBean.parseJsonGroups(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_FINISHED,OrderHelper.getGroupTotalCount(sfGroupBeans)));
                updateSFTotalQuantity(sfGroupBeans);
                sfAdaper.setData(sfGroupBeans);
            }else{
                List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
                EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_FINISHED,orderBeans.size()));
                updateTotalQuantity(orderBeans);
                adapter.setData(orderBeans);
            }

        }
    }


    //订单收回接口
    class RecallQry implements Qry{
        ///{shopid}/order/{orderid}/recall
        private Context context;
        private OrderBean mOrder;

        public RecallQry(Context context, OrderBean mOrder) {
            this.context = context;
            this.mOrder = mOrder;
        }

        @Override
        public void doRequest() {

            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+mOrder.getId()+"/recall?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "RecallQry:resp =" + resp);
            if(resp == null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            if(resp.status==0){
                ToastUtil.showToast(context,R.string.do_success);
                if(LoginHelper.isSFMode()){
                    for(SFGroupBean sfGroup:sfAdaper.groupList){
                        if(mOrder.getGroupId()==sfGroup.getId()){
                            for(OrderBean orderBean:sfGroup.getItemGroup()){
                                orderBean.setStatus(OrderStatus.UNASSIGNED);
                            }
                            break;
                        }
                    }
                }else{
                    for(OrderBean order:adapter.list){
                        if(mOrder.getId() == order.getId()){
                            order.setStatus(OrderStatus.UNASSIGNED);
                            break;
                        }
                    }
                }

                EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
            }else{
                ToastUtil.showToast(context, resp.message);
            }
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

            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+mOrder.getId()+"/deliver?token="+token;
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
                    adapter.removeOrderFromList(mOrder.getId(), adapter.cacheProducedList);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.SCANCODE,1));
                }

            }
        }
    }

    //评论数量接口
    class CommentCountQry implements Qry{

        private Context context;

        public CommentCountQry(Context context) {
            this.context = context;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/feedback/count?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, false);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "CommentCountQry:resp = " + resp);
            if(resp==null){
                return;
            }

            if(resp.status==0){
               int positive = resp.data.optInt("positive");
               int negative = resp.data.optInt("negative");
                updateCommentCount(positive,negative);
            }else{

            }


        }
    }

    private void updateCommentCount(int positive,int negative){
        goodCommentText.setText("好评"+positive);
        badCommentText.setText("差评"+negative);
    }

    //接收自动刷单的广播
    class OrdersReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"收自动刷新的广播消息");
            requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true, false);
        }
    }

    //发送自动刷单语音提示
    private void sendNotificationForAutoNewOrders(boolean isAuto){
        if(mNotificationManager==null){
            mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.app_icon)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setContentTitle("连咖啡消息通知");

        if(isAuto) {
            mBuilder.setContentText("自动刷单，有新订单");
            mBuilder.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.coffee_box));
        }
        Random ran =new Random(System.currentTimeMillis());
        final int notifyId  = ran.nextInt();
        Log.d(TAG,"notifyId = "+notifyId);
        mNotificationManager.notify(notifyId, mBuilder.build());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                mNotificationManager.cancel(notifyId);
            }
        }, 2*60*1000);
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
                case R.id.btn_check_comments:
                    //用户评价
                    InfoDetailDialog.getInstance(mContext).show(mOrderId);
                    break;
            }
        }
    }

    //开始生产接口
    public class startProduceQry implements Qry{
        private Context context;
        private OrderBean mOrder;
        private boolean isShowDlg = true;

        public startProduceQry(Context context, OrderBean mOrder) {
            this.context = context;
            this.mOrder = mOrder;
        }

        public startProduceQry(Context context, OrderBean mOrder, boolean isShowDlg) {
            this.context = context;
            this.mOrder = mOrder;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            int shopId = LoginHelper.getShopId(context);
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+mOrder.getId()+"/beginproduce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"startProduceQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context, R.string.do_success);
                if(LoginHelper.isSFMode()){
                    sfAdaper.changeAndRemoveOrderFromList(mOrder.getId(), OrderStatus.PRODUCING);
                }else{
                    adapter.removeOrderFromList(mOrder.getId(), adapter.cacheToProduceList);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,1));
                }
            }else{
                ToastUtil.showToast(context, resp.message);
            }
        }
    }

    //生产完成操作接口
    public class DoFinishProduceQry implements Qry{
        private Context context;
        private OrderBean mOrder;
        private boolean isShowDlg = true;

        public DoFinishProduceQry(Context context, OrderBean mOrder) {
            this.context = context;
            this.mOrder = mOrder;
        }

        public DoFinishProduceQry(Context context, OrderBean mOrder, boolean isShowDlg) {
            this.context = context;
            this.mOrder = mOrder;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            int shopId = LoginHelper.getShopId(context);
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+mOrder.getId()+"/produce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"DoFinishProduceQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context, R.string.do_success);
                if(LoginHelper.isSFMode()){
                    sfAdaper.changeAndRemoveOrderFromList(mOrder.getId(),OrderStatus.PRODUCED);
                }else{
                    adapter.removeOrderFromList(mOrder.getId(), adapter.cacheProducingList);
                    EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,1));
                }

                //    int leftBatchOrderNum = OrderHelper.removeOrderFromBatchList(orderId);
                //    orderFragment.updateBatchPromptTextView(leftBatchOrderNum);
            }else{
                ToastUtil.showToast(context, resp.message);
            }
        }
    }

    //点击开始生产并打印
    public void startProduceAndPrint(final Context context,final OrderBean order){
        if (order.getInstant() == 0) {
            //预约单
            if (!OrderHelper.isCanHandle(order)) {
                //预约单，生产时间还没到
                SimpleConfirmDialog scd = new SimpleConfirmDialog(context, R.style.MyDialog);
                scd.setContent(R.string.can_not_operate);
                scd.show();
            } else {
                ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                    @Override
                    public void onClickYes() {
                        Log.d(TAG, "orderId = " + order.getOrderSn());
                        //请求服务器改变该订单状态，由 待生产--生产中
                        new startProduceQry(context,order).doRequest();
                        //打印全部
                        PrintHelper.getInstance().printOrderInfo(order);
                        PrintHelper.getInstance().printOrderItems(order);
                    }
                });
                grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
                grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                grabConfirmDialog.show();
            }

        } else {
            //及时单
            ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                @Override
                public void onClickYes() {
                    Log.d(TAG, "orderId = " + order.getOrderSn());
                    //请求服务器改变该订单状态，由 待生产--生产中
                    new startProduceQry(context,order).doRequest();
                    //打印全部
                    PrintHelper.getInstance().printOrderInfo(order);
                    PrintHelper.getInstance().printOrderItems(order);
                }
            });
            grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
            grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
            grabConfirmDialog.show();
        }
    }

    //点击生产完成
    public void finishProduce(final Context context,final OrderBean order){
        if (order.getInstant() == 0) {
            //预约单
            if (!OrderHelper.isCanHandle(order)) {
                //预约单，生产时间还没到
                SimpleConfirmDialog scd = new SimpleConfirmDialog(context, R.style.MyDialog);
                scd.setContent(R.string.can_not_operate);
                scd.show();
            } else {
                ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                    @Override
                    public void onClickYes() {
                        Log.d(TAG, "orderId = " + order.getOrderSn());
                        new DoFinishProduceQry(context,order).doRequest();
                    }
                });
                grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
                grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                grabConfirmDialog.show();
            }

        } else {
            //及时单
            ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                @Override
                public void onClickYes() {
                    Log.d(TAG, "orderId = " + order.getOrderSn());
                    new DoFinishProduceQry(context,order).doRequest();
                }
            });
            grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
            grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
            grabConfirmDialog.show();
        }
    }

    //点击打印
    public void printOrder(Context context,final OrderBean order){
        //打印按钮
        if (order.getInstant() == 0) {
            //预约单
            if (!OrderHelper.isCanHandle(order)) {
                //预约单，打印时间还没到
                SimpleConfirmDialog scd = new SimpleConfirmDialog(context, R.style.MyDialog);
                scd.setContent(R.string.can_not_operate);
                scd.show();
            } else {
                Intent intent = new Intent(context, PrintOrderActivity.class);
                intent.putExtra("order", order);
                context.startActivity(intent);
            }

        } else {
            //及时单
            Intent intent = new Intent(context, PrintOrderActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        }

    }


}
