package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.lyancafe.coffeeshop.activity.PrinterActivity;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.adapter.QueryListRecyclerAdapter;
import com.lyancafe.coffeeshop.adapter.RecyclerAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.ReportWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrderQueryFragment extends Fragment implements View.OnClickListener{

    private static final String TAG  ="OrderQueryFragment";
    private View mContentView;
    private Spinner dateSpinner;
    private Activity mContext;
    private  String[]  dateArray;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private QueryListRecyclerAdapter recyclerAdapter;
    private ReportWindow reportWindow;


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
    private TextView payWayTxt;
    private TextView moneyTxt;
    private TextView userRemarkTxt;
    private TextView csadRemarkTxt;
    private Button finishProduceBtn;
    private Button printOrderBtn;
    private Button moreBtn;
    private Button prevBtn;
    private Button nextBtn;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate");
        getDatesArray(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_order_query,container,false);
        initViews(mContext, mContentView);
        initSpinner(mContext, mContentView);
        return mContentView;
    }

    private void initViews(Context context,View contentView){
        recyclerView = (RecyclerView) contentView.findViewById(R.id.rv_order_list);
        layoutManager = new GridLayoutManager(context,4,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(16, mContext), false));
        ArrayList<OrderBean> orderList = new ArrayList<OrderBean>();
        recyclerAdapter = new QueryListRecyclerAdapter(orderList,mContext,OrderQueryFragment.this);
        recyclerView.setAdapter(recyclerAdapter);
        initDetailView(contentView);

    }

    private void initSpinner(Context context,View contentView){
        dateSpinner = (Spinner) contentView.findViewById(R.id.spinner_date);
        final ArrayAdapter< String> adapter_date = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        for(int i=0;i<dateArray.length;i++){
            if(i==1){
                adapter_date.add("明天 "+dateArray[i]);
            }else if(i==2){
                adapter_date.add("今天 "+dateArray[i]);
            }else if(i==3){
                adapter_date.add("昨天 "+dateArray[i]);
            }else{
                adapter_date.add(dateArray[i]);
            }
        }
        adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter_date);
        dateSpinner.setSelection(0, true);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date_str = dateArray[position];
                if (position > 0) {
                    //请求服务器
                    Log.d(TAG, "请求服务器，上传日期：" + date_str);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    public void updateDetailView(final OrderBean order){
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
            payWayTxt.setText("");
            moneyTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
        }else{
            orderIdTxt.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
            orderReportTxt.setEnabled(true);
            orderReportTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reportWindow==null){
                        reportWindow = new ReportWindow(mContext,null);
                        reportWindow.setOrder(order);
                        reportWindow.showReportWindow(detailRootView);
                    }else{
                        reportWindow.setOrder(order);
                        reportWindow.showReportWindow(detailRootView);
                    }



                }
            });
            reachTimeTxt.setText(order.getInstant()==1?"尽快送达":OrderHelper.getDateToMonthDay(order.getExpectedTime()));
            final long mms = order.getProduceEffect();
            if(OrdersFragment.subTabIndex==0){
                finishProduceBtn.setEnabled(true);
                if(mms<=0){
                    produceEffectTxt.setTextColor(Color.parseColor("#e2435a"));
                    finishProduceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
                    produceEffectTxt.setText("+"+OrderHelper.getDateToMinutes(Math.abs(mms)));
                }else{
                    if(order.getInstant()==0){
                        finishProduceBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                    }else{
                        finishProduceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                    }
                    produceEffectTxt.setTextColor(Color.parseColor("#000000"));
                    produceEffectTxt.setText(OrderHelper.getDateToMinutes(mms));
                }

            }else{
                finishProduceBtn.setEnabled(false);
                finishProduceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                produceEffectTxt.setTextColor(Color.parseColor("#000000"));
                produceEffectTxt.setText("-----");
            }

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
            payWayTxt.setText(order.getPayChannelStr());
            moneyTxt.setText(OrderHelper.getMoneyStr(order.getPaid()));
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            finishProduceBtn.setEnabled(false);
            /*finishProduceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //生产完成
                    ConfirmDialog grabConfirmDialog = new ConfirmDialog(mContext, R.style.MyDialog, new ConfirmDialog.OnClickYesListener(){
                        @Override
                        public void onClickYes() {
                            Log.d(TAG, "orderId = " + order.getOrderSn());
                        //    adapter.new DoFinishProduceQry(order.getId()).doRequest();
                        }
                    });
                    grabConfirmDialog.setContent("订单 "+order.getOrderSn()+" 生产完成？");
                    grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                    grabConfirmDialog.show();
                }
            });*/
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
                            //        new RecallQry(mContext, order.getId()).doRequest();
                                    break;
                                case R.id.menu_scan_code:
                            //        new ScanCodeQry(mContext, order.getId()).doRequest();
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
    private void getDatesArray(Context context){
        dateArray = new String[8];
        dateArray[0] = "按日期查询";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.roll(java.util.Calendar.DAY_OF_YEAR,+1);
        dateArray[1] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        dateArray[2] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        dateArray[3] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        dateArray[4] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        dateArray[5] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        dateArray[6] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        dateArray[7] = sdf.format(cal.getTime());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    //    recyclerAdapter.setData(OrderGridViewAdapter.testList);
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
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_prev:  //上一单
                if(recyclerAdapter.selected>0){
                    recyclerAdapter.selected -= 1;
                    updateDetailView(recyclerAdapter.itemList.get(recyclerAdapter.selected));
                    recyclerAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerAdapter.selected);


                }
                break;
            case R.id.btn_next:  //下一单
                if(recyclerAdapter.selected<recyclerAdapter.itemList.size()-1 && recyclerAdapter.selected!=-1 ){
                    recyclerAdapter.selected += 1;
                    updateDetailView(recyclerAdapter.itemList.get(recyclerAdapter.selected));
                    recyclerAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerAdapter.selected);
                }
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
}
