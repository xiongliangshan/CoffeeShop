package com.lyancafe.coffeeshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.AssignOrderActivity;
import com.lyancafe.coffeeshop.activity.PrintOrderActivity;
import com.lyancafe.coffeeshop.adapter.ProduceFragmentPagerAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.CancelOrderEvent;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdatePrintStatusEvent;
import com.lyancafe.coffeeshop.event.UpdateProduceFragmentTabOrderCount;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{

    private static final String TAG  ="OrdersFragment";
    private Context mContext;
    public static int tabIndex = 0;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProduceFragmentPagerAdapter mPagerAdapter;

    private TextView batchHandleBtn;
    private TextView batchPromptText;

    private IndoDetailListener indoDetailListener;

    /**
     * 订单详情页UI组件
     */
    private TextView orderIdTxt;
    private TextView wholeOrderText;
    private TextView orderTimeTxt;
    private TextView reachTimeTxt;
    private TextView receiveNameTxt;
    private TextView receivePhoneTxt;
    private TextView receiveAddressTxt;
    private TextView deliverNameTxt;
    private TextView deliverPhoneTxt;
    private LinearLayout itemsContainerLayout;
    private LinearLayout userRemarkLayout;
    private TextView userRemarkTxt;
    private LinearLayout csadRemarkLayout;
    private TextView csadRemarkTxt;
    private LinearLayout twoBtnLayout;
    private LinearLayout oneBtnLayout;
    private TextView assignBtn;
    private Button produceAndPrintBtn;
    private Button finishProduceBtn;
    private Button printOrderBtn;

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
        indoDetailListener = new IndoDetailListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(contentView);
        initDetailView(contentView);
        EventBus.getDefault().register(this);
        return contentView;
    }



    private void initViews(View contentView){
        tabLayout  = (TabLayout) contentView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_container);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ToProduceFragment());
        fragments.add(new ProducingFragment());
        mPagerAdapter = new ProduceFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

      /*  refreshbtn = (TextView) contentView.findViewById(R.id.btn_refresh);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    //            requestData(mContext, true, true);
            }
        });*/


       /* ordersGridView = (RecyclerView) contentView.findViewById(R.id.gv_order_list);
        if(LoginHelper.isSFMode()){
            mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
            sfAdaper = new OrderListViewAdapter(getActivity());
            ordersGridView.setAdapter(sfAdaper);
        }else{
            mLayoutManager = new GridLayoutManager(mContext,4,GridLayoutManager.VERTICAL,false);
            adapter = new OrderGridViewAdapter(mContext);
            ordersGridView.setAdapter(adapter);
        }

        ordersGridView.setLayoutManager(mLayoutManager);
        ordersGridView.setHasFixedSize(true);
        ordersGridView.setItemAnimator(new DefaultItemAnimator());
        ordersGridView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, mContext), false));


        initTabButtons(contentView);
        initDetailView(contentView);
        initSpinner(contentView, mContext);

        batchPromptText = (TextView) contentView.findViewById(R.id.tv_batch_prompt);

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
        });*/

    }



    private void initDetailView(View contentView){
        orderIdTxt = (TextView) contentView.findViewById(R.id.order_id);
        wholeOrderText = (TextView) contentView.findViewById(R.id.tv_whole_order_sn);
        orderTimeTxt = (TextView) contentView.findViewById(R.id.order_time);
        reachTimeTxt = (TextView) contentView.findViewById(R.id.reach_time);
        receiveNameTxt  = (TextView) contentView.findViewById(R.id.receiver_name);
        receivePhoneTxt = (TextView) contentView.findViewById(R.id.receiver_phone);
        receiveAddressTxt = (TextView) contentView.findViewById(R.id.receiver_address);
        deliverNameTxt = (TextView) contentView.findViewById(R.id.deliver_name);
        deliverPhoneTxt = (TextView) contentView.findViewById(R.id.deliver_phone);
        itemsContainerLayout = (LinearLayout) contentView.findViewById(R.id.items_container_layout);
        userRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_remark);
        userRemarkLayout.setOnClickListener(indoDetailListener);
        userRemarkTxt = (TextView) contentView.findViewById(R.id.user_remark);
        csadRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_csad_remark);
        csadRemarkLayout.setOnClickListener(indoDetailListener);
        csadRemarkTxt = (TextView) contentView.findViewById(R.id.csad_remark);
        twoBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_twobtn);
        oneBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_onebtn);
        assignBtn = (TextView) contentView.findViewById(R.id.btn_assign);
        produceAndPrintBtn = (Button) contentView.findViewById(R.id.btn_produce_print);
        finishProduceBtn = (Button) contentView.findViewById(R.id.btn_finish_produce);
        printOrderBtn = (Button) contentView.findViewById(R.id.btn_print_order);
    }
    private void updateDetailView(final OrderBean order){
        if(order==null){
            orderIdTxt.setText("");
            wholeOrderText.setText("");
            orderTimeTxt.setText("");
            reachTimeTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            deliverNameTxt.setText("");
            deliverPhoneTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            itemsContainerLayout.removeAllViews();
            assignBtn.setVisibility(View.GONE);
            produceAndPrintBtn.setEnabled(false);
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
        }else {
            assignBtn.setVisibility(View.VISIBLE);
            produceAndPrintBtn.setEnabled(true);
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);

            orderIdTxt.setText(OrderHelper.getShopOrderSn(order));
            wholeOrderText.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
            reachTimeTxt.setText(order.getInstant() == 1 ? "尽快送达" : OrderHelper.getDateToMonthDay(order.getExpectedTime()));
            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            deliverNameTxt.setText(order.getCourierName());
            deliverPhoneTxt.setText(order.getCourierPhone());

            fillItemListData(itemsContainerLayout, order);

            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
                assignBtn.setVisibility(View.GONE);
            } else {
                assignBtn.setVisibility(View.VISIBLE);
                if (order.getStatus() == OrderStatus.UNASSIGNED) {
                    assignBtn.setText("订单指派");
                    assignBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AssignOrderActivity.class);
                            intent.putExtra("orderId", order.getId());
                            mContext.startActivity(intent);
                        }
                    });
                } else if (order.getStatus() == OrderStatus.ASSIGNED) {
                    assignBtn.setText("订单撤回");
                    assignBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HttpHelper.getInstance().reqRecallOrder(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {
                                @Override
                                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                                    handleRecallOrderResponse(xlsResponse, call, response);
                                }
                            });
                        }
                    });
                }
            }

            if (order.getProduceStatus() == OrderStatus.UNPRODUCED) {
                twoBtnLayout.setVisibility(View.GONE);
                oneBtnLayout.setVisibility(View.VISIBLE);
                produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击开始生产（打印）按钮
                        EventBus.getDefault().post(new StartProduceEvent(order));
                    }
                });
            } else if (order.getProduceStatus() == OrderStatus.PRODUCING) {
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
                if (OrderHelper.isPrinted(mContext, order.getOrderSn())) {
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                } else {
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new PrintOrderEvent(order));
                    }
                });
            } else {
                twoBtnLayout.setVisibility(View.VISIBLE);
                oneBtnLayout.setVisibility(View.GONE);
                finishProduceBtn.setVisibility(View.GONE);
                if (OrderHelper.isPrinted(mContext, order.getOrderSn())) {
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                } else {
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


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabIndex = tab.getPosition();
        viewPager.setCurrentItem(tabIndex,false);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Subscribe
    public void OnUpdatePrintStatusEvent(UpdatePrintStatusEvent event){
        Log.d(TAG, "OnUpdatePrintStatusEvent,orderSn = " + event.orderSn);
        //打印界面退出的时候，刷新一下打印按钮文字

    }

    /**
     * 更新列表订单数量
     * @param event
     */
    @Subscribe
    public void OnUpdateProduceFragmentTabOrderCountEvent(UpdateProduceFragmentTabOrderCount event){
        Log.d("xls","UpdateProduceFragmentTabOrderCount");
        TabLayout.Tab tab = tabLayout.getTabAt(event.tabIndex);
        tab.setTag(event.count);
        if(event.count>0){
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex)+"("+event.count+")");
        }else{
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex));
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




    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:");

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }



    /**
     * 对订单操作后更改相关列表中的订单数量角标显示
     * @param event
     */
    @Subscribe
    public void onChangeTabCountByActionEvent(ChangeTabCountByActionEvent event){
        TabLayout.Tab tabToproduce = tabLayout.getTabAt(0);
        TabLayout.Tab tabProducing = tabLayout.getTabAt(1);
        int toProduceCount = ((Integer) tabToproduce.getTag()).intValue();
        int producingCount = ((Integer) tabProducing.getTag()).intValue();
        switch (event.action){
            case OrderAction.STARTPRODUCE:
                int tabTo = toProduceCount-event.count;
                int tabPro = producingCount+event.count;
                tabToproduce.setText(mPagerAdapter.getPageTitle(0)+"("+tabTo+")");
                tabProducing.setText(mPagerAdapter.getPageTitle(1)+"("+tabPro+")");
                tabToproduce.setTag(tabTo);
                tabProducing.setTag(tabPro);
                break;
            case OrderAction.FINISHPRODUCE:
                int tabProduceResult = producingCount-event.count;
                tabProducing.setText(mPagerAdapter.getPageTitle(1)+"("+tabProduceResult+")");
                tabProducing.setTag(tabProduceResult);
                break;
        }
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





    //处理服务器返回数据---订单撤回
    private void handleRecallOrderResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            ToastUtil.showToast(getActivity(), R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            EventBus.getDefault().post(new RecallOrderEvent(tabIndex,id));
        }else{
            ToastUtil.showToast(getActivity(), xlsResponse.message);
        }
    }




    class IndoDetailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_user_remark:
                    //用户备注
                    new InfoDetailDialog(getActivity()).show(userRemarkTxt.getText().toString());
                    break;
                case R.id.ll_csad_remark:
                    //客服备注
                    new InfoDetailDialog(getActivity()).show(csadRemarkTxt.getText().toString());
                    break;
            }
        }
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


    @Override
    protected void onVisible() {
 //      Log.d("xls","OrdersFragment is onVisible");
        if(!isResumed()){
            return;
        }
        for(int i=0;i<mPagerAdapter.getCount();i++){
            if(i==tabIndex){
                mPagerAdapter.getItem(i).setUserVisibleHint(true);
            }else{
                mPagerAdapter.getItem(i).setUserVisibleHint(false);
            }
        }
    }

    @Override
    protected void onInVisible() {
 //       Log.d("xls","OrdersFragment is onInVisible");
    }
}
