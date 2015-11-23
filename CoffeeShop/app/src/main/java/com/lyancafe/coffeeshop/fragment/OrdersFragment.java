package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.HomeActivity;
import com.lyancafe.coffeeshop.activity.PrinterActivity;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.service.AutoFetchOrdersService;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.ListTabButton;
import com.lyancafe.coffeeshop.widget.ReportWindow;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import org.json.JSONArray;

import java.util.ArrayList;
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
    private ListTabButton haveDoneTab;
    private ListTabButton deliveringTab;
    private ListTabButton deliveryFinishedTab;

    private ListTabButtonListener ltbListener;

    private Spinner sortSpinner;
    private Spinner categorySpinner;

    private GridView ordersGridView;
    private OrderGridViewAdapter adapter;

    private Button refreshbtn;
    private TextView totalQuantityTxt;
    private long starttime;
    private long endtime;

    private int orderBy = 0;
    private int fillterInstant = 0;

    private ReportWindow reportWindow;
    NotificationManager mNotificationManager;

    /**
     * 订单详情页UI组件
     */
    private LinearLayout detailRootView;
    private TextView orderIdTxt;
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
    private TextView orderPriceTxt;
    private TextView payWayTxt;
    private TextView moneyTxt;
    private TextView userRemarkTxt;
    private TextView csadRemarkTxt;
    private Button finishProduceBtn;
    private Button printOrderBtn;
    private Button moreBtn;
    private Button prevBtn;
    private Button nextBtn;

    private OrdersReceiver ordersReceiver;
    //消息处理
    private static final int MSG_UPDATE_ORDER_NUMBER = 101;
    private static final int MSG_ACTION_PRODUCE = 102;
    private static final int MSG_ACTION_SCANCODE = 103;
    //定义操作
    public static final int ACTION_PRODUCE = 301; //点击生产完成
    public static final int ACTION_SCANCODE = 302;//点击扫码交付
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_ORDER_NUMBER:
                    int whichTab = msg.arg1;
                    int num = msg.arg2;
                    if(whichTab==0){
                        toDoTab.setCount(num);
                    }else if(whichTab==1){
                        haveDoneTab.setCount(num);
                    }else if(whichTab==2){
                        deliveringTab.setCount(num);
                    }else if(whichTab==3){
                        deliveryFinishedTab.setCount(num);
                    }
                    break;
                case MSG_ACTION_PRODUCE:
                    toDoTab.setCount(toDoTab.getCount()-1);
                    haveDoneTab.setCount(haveDoneTab.getCount()+1);
                    break;
                case MSG_ACTION_SCANCODE:
                    haveDoneTab.setCount(haveDoneTab.getCount()-1);
                    deliveryFinishedTab.setCount(deliveryFinishedTab.getCount()+1);
                    break;
            }
        }
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
        context.registerReceiver(ordersReceiver,filter);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(mContentView);
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
                Log.d(TAG, "点击了 " + position);
            }
        });

        initTabButtons(contentView);
        initDetailView(contentView);
        initSpinner(contentView, mContext);

        refreshbtn = (Button) contentView.findViewById(R.id.btn_refresh);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true,true);
            }
        });
        totalQuantityTxt = (TextView) contentView.findViewById(R.id.total_quantity);
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
                new OrderProducedQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 2:
                new OrderDeliveryingQry(context, orderBy, fillterInstant,isShowProgress).doRequest();
                break;
            case 3:
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
        orderPriceTxt = (TextView) contentView.findViewById(R.id.order_price);
        payWayTxt = (TextView) contentView.findViewById(R.id.pay_way);
        moneyTxt = (TextView) contentView.findViewById(R.id.money);
        userRemarkTxt = (TextView) contentView.findViewById(R.id.user_remark);
        csadRemarkTxt = (TextView) contentView.findViewById(R.id.csad_remark);
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
            orderTimeTxt.setText("");
            orderReportTxt.setEnabled(false);
            reachTimeTxt.setText("");
            produceEffectTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            deliverNameTxt.setText("");
            deliverPhoneTxt.setText("");
            fillItemListData(itemsContainerLayout, new ArrayList<ItemContentBean>());
            orderPriceTxt.setText("");
            payWayTxt.setText("");
            moneyTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
        }else{
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);
            moreBtn.setEnabled(true);

            orderIdTxt.setText(order.getOrderSn());
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
            reachTimeTxt.setText(order.getInstant()==1?"尽快送达":OrderHelper.getDateToMonthDay(order.getExpectedTime()));

            OrderHelper.showEffect(order, finishProduceBtn, produceEffectTxt);

            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            deliverNameTxt.setText(order.getCourierName());
            deliverPhoneTxt.setText(order.getCourierPhone());
            if(order.getStatus()== OrderHelper.UNASSIGNED_STATUS){
                deliverInfoContainerLayout.setVisibility(View.GONE);
            }else {
                deliverInfoContainerLayout.setVisibility(View.VISIBLE);
            }

            fillItemListData(itemsContainerLayout, order.getItems());
            orderPriceTxt.setText("应付: "+OrderHelper.getMoneyStr(OrderHelper.getTotalPrice(order)));
            payWayTxt.setText(order.getPayChannelStr());
            moneyTxt.setText(OrderHelper.getMoneyStr(order.getPaid()));
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            finishProduceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //生产完成
                    ConfirmDialog grabConfirmDialog = new ConfirmDialog(mContext, R.style.MyDialog, new ConfirmDialog.OnClickYesListener(){
                        @Override
                        public void onClickYes() {
                            Log.d(TAG, "orderId = " + order.getOrderSn());
                            adapter.new DoFinishProduceQry(order.getId()).doRequest();
                        }
                    });
                    grabConfirmDialog.setContent("订单 "+order.getOrderSn()+" 生产完成？");
                    grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                    grabConfirmDialog.show();
                }
            });
            if(CoffeeShopApplication.getInstance().printedSet.contains(order.getOrderSn())){
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
                    Intent intent = new Intent(mContext, PrinterActivity.class);
                    intent.putExtra("order",order);
                    mContext.startActivity(intent);
                }
            });
            moreBtn.setEnabled(true);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.inflate(R.menu.menu_order_detail_more);
                    if(!order.isWxScan()){
                        popup.getMenu().findItem(R.id.menu_scan_code).setVisible(false);
                    }
                    if(order.getStatus()!=OrderHelper.ASSIGNED_STATUS){
                        popup.getMenu().findItem(R.id.menu_undo_order).setVisible(false);
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
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(mContext);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            tv1.setMaxEms(7);
            tv1.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
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
        ll.invalidate();
    }
    private void initTabButtons(View contentView){
        toDoTab = (ListTabButton) contentView.findViewById(R.id.tab_to_do);
        haveDoneTab = (ListTabButton) contentView.findViewById(R.id.tab_have_done);
        deliveringTab = (ListTabButton) contentView.findViewById(R.id.tab_delivering);
        deliveryFinishedTab = (ListTabButton) contentView.findViewById(R.id.tab_delivery_finished);

        toDoTab.setClickBg(true);
        haveDoneTab.setClickBg(false);
        deliveringTab.setClickBg(false);
        deliveryFinishedTab.setClickBg(false);

        toDoTab.setOnClickListener(ltbListener);
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
                Log.d("xiong", "排序：positon =" + position + "选择了 " + adapter_sort.getItem(position));
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
                Log.d("xiong", "排序：positon =" + position + "选择了 " + adapter_category.getItem(position));
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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        subTabIndex = 0;
        requestData(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL, true,true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:");
        //打印界面退出的时候，刷新一下打印按钮文字
        adapter.notifyDataSetChanged();
        if(adapter.list.size()>0 && adapter.selected>=0){
            updateDetailView(adapter.list.get(adapter.selected));
        }

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
        }else{
            sortSpinner.setVisibility(View.INVISIBLE);
            categorySpinner.setVisibility(View.INVISIBLE);
            refreshbtn.setVisibility(View.INVISIBLE);
        }
    }



    class ListTabButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tab_to_do:
                    toDoTab.setClickBg(true);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(true);
                    subTabIndex = 0;
                    new OrderToProduceQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = 1;
                    new OrderProducedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    showWidget(false);
                    subTabIndex = 2;
                    new OrderDeliveryingQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    showWidget(false);
                    subTabIndex = 3;
                    new OrderFinishedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,true).doRequest();
                    resetSpinners();
                    break;
            }
            if(subTabIndex == 3){
                totalQuantityTxt.setVisibility(View.VISIBLE);
            }else{
                totalQuantityTxt.setVisibility(View.INVISIBLE);
            }
        }
    }


    //更新订单数量显示
    public void updateOrdersNum(int whichTab,int num){
        Message msg =  new Message();
        msg.what = MSG_UPDATE_ORDER_NUMBER;
        msg.arg1 = whichTab;
        msg.arg2 = num;
        mHandler.sendMessage(msg);

    }
    //在某些操作后更新相关联的订单数量
    public void updateOrdersNumAfterAction(int action){
        if(action==ACTION_PRODUCE){
            Message msg =  new Message();
            msg.what = MSG_ACTION_PRODUCE;
            mHandler.sendMessage(msg);
        }else if(action==ACTION_SCANCODE){
            Message msg =  new Message();
            msg.what = MSG_ACTION_SCANCODE;
            mHandler.sendMessage(msg);
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

            starttime = System.currentTimeMillis();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            endtime = System.currentTimeMillis();
            Log.d(TAG,"请求耗时:"+(endtime - starttime));
            Log.d(TAG, "OrderQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
            Log.d(TAG, "orderBeans  =" + orderBeans);
            updateOrdersNum(0, orderBeans.size());
            if(orderBeans.size()>adapter.cacheToProduceList.size() && !isShowProgress){
                sendNotificationForAutoNewOrders(AutoFetchOrdersService.auto_flag);
            }
            adapter.setData(orderBeans);
            if(orderBeans.size()>0){
                updateDetailView(orderBeans.get(0));
            }else{
                updateDetailView(null);
            }
        }
    }

    //生产已完成订单列表接口
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

            starttime = System.currentTimeMillis();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            endtime = System.currentTimeMillis();
            Log.d(TAG,"请求耗时:"+(endtime - starttime));
            Log.d(TAG, "OrderQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
            Log.d(TAG, "orderBeans  =" + orderBeans);
            updateOrdersNum(1,orderBeans.size());
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

            starttime = System.currentTimeMillis();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            endtime = System.currentTimeMillis();
            Log.d(TAG,"请求耗时:"+(endtime - starttime));
            Log.d(TAG, "OrderQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
            Log.d(TAG, "orderBeans  =" + orderBeans);
            updateOrdersNum(2,orderBeans.size());
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

            starttime = System.currentTimeMillis();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            endtime = System.currentTimeMillis();
            Log.d(TAG,"请求耗时:"+(endtime - starttime));
            Log.d(TAG, "OrderQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context,R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
            Log.d(TAG, "orderBeans  =" + orderBeans);
            updateOrdersNum(3, orderBeans.size());
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
            starttime = System.currentTimeMillis();
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
                        order.setStatus(OrderHelper.UNASSIGNED_STATUS);
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
            starttime = System.currentTimeMillis();
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
                adapter.removeOrderFromProducedList(orderId);
                updateOrdersNumAfterAction(OrdersFragment.ACTION_SCANCODE);
            }
        }
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


}
