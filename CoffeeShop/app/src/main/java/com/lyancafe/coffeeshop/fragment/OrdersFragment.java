package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.OrderGridViewAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ListTabButton;
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

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment {

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

    private long starttime;
    private long endtime;

    private int orderBy = 0;
    private int fillterInstant = 0;

    /**
     * 订单详情页UI组件
     */
    private TextView orderIdTxt;
    private TextView orderTimeTxt;
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
    private Button prevBtn;
    private Button nextBtn;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG, "onAttach");
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
        adapter = new OrderGridViewAdapter(mContext);
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
                new OrderQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL).doRequest();
                resetSpinners();
            }
        });
    }

    private void initDetailView(View contentView){
        orderIdTxt = (TextView) contentView.findViewById(R.id.order_id);
        orderTimeTxt = (TextView) contentView.findViewById(R.id.order_time);
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
        prevBtn = (Button) contentView.findViewById(R.id.btn_prev);
        nextBtn = (Button) contentView.findViewById(R.id.btn_next);
    }
    private void updateDetailView(OrderBean order){
        if(order==null){
            orderIdTxt.setText("");
            orderTimeTxt.setText("");
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
        }else{
            orderIdTxt.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
            reachTimeTxt.setText(order.getInstant()==1?"尽快送达":OrderHelper.getDateToMonthDay(order.getExpectedTime()));
            produceEffectTxt.setText(OrderHelper.getDateToMinutes(order.getProduceEffect()));
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
            moneyTxt.setText(order.getPaid()+"");
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
        }

    }
    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(mContext);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            tv1.setMaxEms(7);
            TextView tv2 = new TextView(mContext);
            tv2.setText("X " + item.getQuantity());
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
        sortSpinner.setSelection(0,true);
        categorySpinner.setSelection(0,true);
    }
    private void initSpinner(View contentView,Context context){
        sortSpinner = (Spinner) contentView.findViewById(R.id.spinner_sort);
        categorySpinner = (Spinner) contentView.findViewById(R.id.spinner_category);

        final ArrayAdapter< String> adapter_sort = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_sort.add(context.getResources().getString(R.string.sort_by_produce_effect));
        adapter_sort.add(context.getResources().getString(R.string.sort_by_order_time));
//        adapter_sort.add(context.getResources().getString(R.string.sort_by_delivery_effect));
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter_sort);
        sortSpinner.setSelection(0, true);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("xiong","排序：positon ="+position+"选择了 "+adapter_sort.getItem(position));
                switch (position){
                    case 0:
                        orderBy = OrderHelper.PRODUCE_TIME;
                        break;
                    case 1:
                        orderBy = OrderHelper.ORDER_TIME;
                        break;
                }
                new OrderQry(mContext, orderBy, fillterInstant).doRequest();
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
                Log.d("xiong","排序：positon ="+position+"选择了 "+adapter_category.getItem(position));
                switch (position){
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
                new OrderQry(mContext, orderBy, fillterInstant).doRequest();
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
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    break;
            }
        }
    }



    //刷新订单列表接口
    class OrderQry implements Qry{

        private Context context;
        private int orderBy;
        private int fillterInstant;

        public OrderQry(Context context, int orderBy, int fillterInstant) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
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
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this);
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
            if(orderBeans.size()>0){
                adapter.setData(orderBeans);
                updateDetailView(null);
            }
        }
    }


}
