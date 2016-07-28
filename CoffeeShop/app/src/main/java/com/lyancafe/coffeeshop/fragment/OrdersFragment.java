package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.AssignOrderActivity;
import com.lyancafe.coffeeshop.activity.CommentActivity;
import com.lyancafe.coffeeshop.activity.LocationActivity;
import com.lyancafe.coffeeshop.activity.PrintOrderActivity;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.CancelOrderEvent;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.ClickCommentEvent;
import com.lyancafe.coffeeshop.event.CommentCountEvent;
import com.lyancafe.coffeeshop.event.CommitIssueOrderEvent;
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
    public static int subTabIndex = 0;

    private ListTabButton toDoTab;
    private ListTabButton doingTab;
    private ListTabButton haveDoneTab;
    private ListTabButton deliveringTab;
    private ListTabButton deliveryFinishedTab;

    private TextView shopNameText;
    private ListTabButtonListener ltbListener;

    private Spinner sortSpinner;
    private Spinner categorySpinner;

    private GridView ordersGridView;
    private OrderGridViewAdapter adapter;

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
    private TextView orderPriceTxt;
    private TextView payWayTxt;
    private TextView moneyTxt;
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
        ordersGridView = (GridView) contentView.findViewById(R.id.gv_order_list);
        adapter = new OrderGridViewAdapter(mContext,OrdersFragment.this);
        ordersGridView.setAdapter(adapter);
        ordersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selected = position;
                adapter.notifyDataSetChanged();
                updateDetailView(adapter.list.get(position));
            }
        });

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
                                adapter.new DoFinishProduceQry(OrderHelper.batchList.get(i).getId(), false).doRequest();
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

        totalQuantityTxt.setText("总杯量:"+sum);
    }
    private void requestData(Context context, int orderBy, int fillterInstant,boolean isRefresh,boolean isShowProgress){
        switch (subTabIndex){
            case 0:
                new OrderToProduceQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 1:
                new OrderProducingQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 2:
                new OrderProducedQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 3:
                new OrderDeliveryingQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 4:
                new OrderFinishedQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
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
        orderPriceTxt = (TextView) contentView.findViewById(R.id.order_price);
        payWayTxt = (TextView) contentView.findViewById(R.id.pay_way);
        moneyTxt = (TextView) contentView.findViewById(R.id.money);
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
            deliverLocationInfoTxt.setVisibility(View.GONE);
            fillItemListData(itemsContainerLayout, order);
            orderPriceTxt.setText("");
            payWayTxt.setText("");
            moneyTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            userCommentTagsText.setText("");
            userCommentContentText.setText("");
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
        }else{
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

            if(OrdersFragment.subTabIndex==0){
                if(order.getInstant()==0){
                    produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                }else{
                    produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                }
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
            orderPriceTxt.setText("应付: " + OrderHelper.getMoneyStr(OrderHelper.getTotalPrice(order)));
            payWayTxt.setText(order.getPayChannelStr());
            moneyTxt.setText(OrderHelper.getMoneyStr(order.getPaid()));
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            userCommentTagsText.setText(OrderHelper.getCommentTagsStr(order.getFeedbackTags()));
            userCommentContentText.setText(order.getFeedback());

            if(OrdersFragment.subTabIndex == 0){
                oneBtnLayout.setVisibility(View.VISIBLE);
                twoBtnLayout.setVisibility(View.GONE);
                produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击开始生产（打印）按钮
                        startProduceAndPrint(mContext,order);
                    }
                });
            }else{
                oneBtnLayout.setVisibility(View.GONE);
                twoBtnLayout.setVisibility(View.VISIBLE);
                if(OrdersFragment.subTabIndex!=1){
                    finishProduceBtn.setEnabled(false);
                }else{
                    finishProduceBtn.setEnabled(true);
                }
                finishProduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //生产完成
                        finishProduce(mContext,order);
                    }
                });
                if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                }else{
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setEnabled(true);
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打印
                        printOrder(mContext,order);
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
                                    new RecallQry(mContext, order.getId()).doRequest();
                                    break;
                                case R.id.menu_scan_code:
                                    new ScanCodeQry(mContext, order.getId()).doRequest();
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
            tv1.setText(item.getProduct()+OrderHelper.getLabelStr(item.getRecipeFittingsList()));
            tv1.setMaxEms(9);
            tv1.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv1.setTextColor(mContext.getResources().getColor(R.color.font_black));
            TextView tv2 = new TextView(mContext);
            tv2.setText("X " + item.getQuantity());
            tv2.getPaint().setFakeBoldText(true);
            tv2.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            RelativeLayout rl = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(5,mContext);;
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(5,mContext);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4,mContext);
            ll.addView(rl,lp);
        }
        if(order.getGift()==2){
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
            lp3.leftMargin = OrderHelper.dip2Px(5,mContext);;
            tv3.setLayoutParams(lp3);
            r2.addView(tv3);

            RelativeLayout.LayoutParams lp4=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp4.rightMargin = OrderHelper.dip2Px(5,mContext);
            tv4.setLayoutParams(lp4);
            r2.addView(tv4);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4,mContext);
            ll.addView(r2, lp);
        }

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
        orderBy = OrderHelper.PRODUCE_TIME;
        fillterInstant = OrderHelper.ALL;
        sortSpinner.setSelection(0, true);
        categorySpinner.setSelection(0, true);
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
                switch (position){
                    case 0:
                        orderBy = OrderHelper.PRODUCE_TIME;
                        break;
                    case 1:
                        orderBy = OrderHelper.ORDER_TIME;
                        break;
                }
                requestData(mContext, orderBy, fillterInstant, false,true);
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

    }

    @Subscribe
    public void OnUpdatePrintStatusEvent(UpdatePrintStatusEvent event){
        Log.d(TAG,"OnUpdatePrintStatusEvent,orderSn = "+event.orderSn);
        //打印界面退出的时候，刷新一下打印按钮文字
        adapter.notifyDataSetChanged();
        if(adapter.list.size()>0 && adapter.selected>=0 && adapter.selected<adapter.list.size()){
            updateDetailView(adapter.list.get(adapter.selected));
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
        new CommentCountQry(mContext).doRequest();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        subTabIndex = 0;
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
        if(adapter.timer!=null){
            adapter.timer.cancel();
        }
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
                    updateDetailView(adapter.list.get(adapter.selected));
                    adapter.notifyDataSetChanged();
                    ordersGridView.smoothScrollToPosition(adapter.selected);

                }
                break;
            case R.id.btn_next:  //下一单
                if(adapter.selected<adapter.list.size()-1 && adapter.selected!=-1 ){
                    adapter.selected += 1;
                    updateDetailView(adapter.list.get(adapter.selected));
                    adapter.notifyDataSetChanged();
                    ordersGridView.smoothScrollToPosition(adapter.selected);
                }
                break;
        }

    }

    //隐藏spinner和刷新按钮
    private void showWidget(boolean isShow){
        if(isShow){
            sortSpinner.setVisibility(View.VISIBLE);
            categorySpinner.setVisibility(View.VISIBLE);
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
                    subTabIndex = 0;
                    new OrderToProduceQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_doing:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(true);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(true);
                    subTabIndex = 1;
                    new OrderProducingQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = 2;
                    new OrderProducedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = 3;
                    new OrderDeliveryingQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    doingTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    showWidget(false);
                    subTabIndex = 4;
                    new OrderFinishedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
            }
            if(subTabIndex == 4){
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
    public void onChangeTabCountByActionEvent(ChangeTabCountByActionEvent event){
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


    /**
     * 提交问题订单后立即更新订单显示状态
     * @param event
     */
    public void onCommitIssueOrderEvent(CommitIssueOrderEvent event){
        long orderId = event.orderId;
        for(int i=0;i<adapter.list.size();i++){
            if(adapter.list.get(i).getId()==orderId){
                adapter.list.get(i).setIssueOrder(true);
                break;
            }
        }
        adapter.notifyDataSetChanged();
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

        public OrderToProduceQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/toproduce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderToProduceQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
        //    updateOrdersNum(0, orderBeans.size());
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_TOPRODUCE,orderBeans.size()));
            if(orderBeans.size()>adapter.cacheToProduceList.size() && !isShowProgress){
                sendNotificationForAutoNewOrders(true);
            }
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
            //检查列表中是否有未完成的合并订单
            if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                OrderHelper.batchList.clear();
                updateBatchPromptTextView(0);
            }

        }
    }

    //生产中列表接口
    class OrderProducingQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;

        public OrderProducingQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/producing?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "OrderQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
        //    updateOrdersNum(1, orderBeans.size());
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCING,orderBeans.size()));
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
            //检查列表中是否有未完成的合并订单
            if(!OrderHelper.isContainerBatchOrder(orderBeans)){
                OrderHelper.batchList.clear();
                updateBatchPromptTextView(0);
            }

        }
    }

    //已生产订单列表接口
    class OrderProducedQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;

        public OrderProducedQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/produced?token="+token;
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
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
        //    updateOrdersNum(2,orderBeans.size());
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,orderBeans.size()));
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
        }
    }

    //配送中订单列表接口
    class OrderDeliveryingQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        public OrderDeliveryingQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/delivering?token="+token;
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
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
        //    updateOrdersNum(3,orderBeans.size());
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_DELIVERING,orderBeans.size()));
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
        }
    }

    //已完成订单列表接口
    class OrderFinishedQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        public OrderFinishedQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/finished?token="+token;
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
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
        //    updateOrdersNum(4, orderBeans.size());
            EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_FINISHED,orderBeans.size()));
            updateTotalQuantity(orderBeans);
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
        }
    }


    //订单收回接口
    class RecallQry implements Qry{
        ///{shopid}/order/{orderid}/recall
        private Context context;
        private long orderId;

        public RecallQry(Context context, long orderId) {
            this.context = context;
            this.orderId = orderId;
        }

        @Override
        public void doRequest() {

            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/recall?token="+token;
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
                for(OrderBean order:adapter.cacheToProduceList){
                    if(orderId == order.getId()){
                        order.setStatus(OrderStatus.UNASSIGNED);
                        break;
                    }
                }
                adapter.list = adapter.cacheToProduceList;
                adapter.notifyDataSetChanged();
            }else{
                ToastUtil.showToast(context, resp.message);
            }
        }
    }

    //扫码交付接口
    class ScanCodeQry implements Qry{
        private Context context;
        private long orderId;

        public ScanCodeQry(Context context, long orderId) {
            this.context = context;
            this.orderId = orderId;
        }

        @Override
        public void doRequest() {

            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/deliver?token="+token;
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
                adapter.removeOrderFromList(orderId, adapter.cacheProducedList);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.SCANCODE));
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
            Log.d("AutoFetchOrdersService","收到广播消息");
            requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true,false);
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
                case R.id.ll_user_comment:
                    //用户评价
                    InfoDetailDialog.getInstance(mContext).show(userCommentTagsText.getText().toString()+"\n"+userCommentContentText.getText().toString());
                    break;
            }
        }
    }


    //开始生产并打印
    public void startProduceAndPrint(Context context,final OrderBean order){
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
                        adapter.new startProduceQry(order.getId()).doRequest();
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
                    adapter.new startProduceQry(order.getId()).doRequest();
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

    //生产完成
    public void finishProduce(Context context,final OrderBean order){
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
                        adapter.new DoFinishProduceQry(order.getId()).doRequest();
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
                    adapter.new DoFinishProduceQry(order.getId()).doRequest();
                }
            });
            grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
            grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
            grabConfirmDialog.show();
        }
    }

    //打印
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
