package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/12/25.
 */

public class DetailView extends CardView implements View.OnClickListener{

    private LinearLayout rootLayout;
    private TextView tvShopNo;
    private TextView tvReachTime;
    private ConstraintLayout clReplenish;
    private TextView tvRelationOrderId;
    private TextView tvReason;
    private TextView tvReceiverName;
    private TextView tvReceiverPhone;
    private TextView ultIssueFeedback;
    private TextView tvOrderTime;
    private TextView tvOrderId;
    private TextView tvReceiverAddress;
    private TextView tvDeliverTeam;
    private TextView tvDeliverName;
    private TextView tvDeliverPhone;
    private ScrollView svCoffee;
    private LinearLayout llItemsContainer;
    private LinearLayout llCustomerRemark;
    private TextView  tvCustomerRemark;
    private LinearLayout llCsadRemark;
    private TextView tvCsadRemark;
    private RelativeLayout llButtonContainer;
    private LinearLayout llTwoButton;
    private TextView btnFinishProduce;
    private TextView btnPrint;
    private LinearLayout llOneButton;
    private TextView btnProducePrint;


    private OrderBean mOrder;

    private ActionCallback callback;

    public DetailView(Context context) {
        super(context);
        initView(context);
    }

    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_detail,this,true);
        setBackground(ContextCompat.getDrawable(context,R.drawable.bg_order));
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        tvShopNo = (TextView) findViewById(R.id.tv_shop_no);
        tvReachTime = (TextView) findViewById(R.id.tv_reach_time);
        clReplenish = (ConstraintLayout) findViewById(R.id.cl_replenish);
        tvRelationOrderId = (TextView) findViewById(R.id.tv_relationOrderId);
        tvReason = (TextView) findViewById(R.id.tv_reason);
        tvReceiverName = (TextView) findViewById(R.id.tv_receiver_name);
        tvReceiverPhone = (TextView) findViewById(R.id.tv_receiver_phone);
        ultIssueFeedback = (TextView) findViewById(R.id.ult_issue_feedback);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvReceiverAddress = (TextView) findViewById(R.id.tv_receiver_address);
        tvDeliverTeam = (TextView) findViewById(R.id.tv_deliver_team);
        tvDeliverName = (TextView) findViewById(R.id.tv_deliver_name);
        tvDeliverPhone = (TextView) findViewById(R.id.tv_deliver_phone);
        svCoffee = (ScrollView) findViewById(R.id.sv_coffee);
        llItemsContainer = (LinearLayout) findViewById(R.id.ll_items_container);
        llCustomerRemark = (LinearLayout) findViewById(R.id.ll_customer_remark);
        tvCustomerRemark = (TextView) findViewById(R.id.tv_customer_remark);
        llCsadRemark = (LinearLayout) findViewById(R.id.ll_csad_remark);
        tvCsadRemark = (TextView) findViewById(R.id.tv_csad_remark);
        llButtonContainer = (RelativeLayout) findViewById(R.id.ll_button_container);
        llTwoButton = (LinearLayout) findViewById(R.id.ll_two_button);
        btnFinishProduce = (TextView) findViewById(R.id.btn_finish_produce);
        btnPrint = (TextView) findViewById(R.id.btn_print);
        llOneButton = (LinearLayout) findViewById(R.id.ll_one_button);
        btnProducePrint = (TextView) findViewById(R.id.btn_produce_print);

        setListener();
    }

    private void setListener(){
        ultIssueFeedback.setOnClickListener(this);
        btnFinishProduce.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnProducePrint.setOnClickListener(this);
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    /**
     * 更新数据
     * @param order
     */
    public void updateData(OrderBean order){
        mOrder=order;
        if(order==null){
            tvShopNo.setText("");
            tvReachTime.setText("");
            clReplenish.setVisibility(View.GONE);
            tvReceiverName.setText("");
            tvReceiverPhone.setText("");
            ultIssueFeedback.setEnabled(true);
            tvOrderTime.setText("");
            tvOrderId.setText("");
            tvReceiverAddress.setText("");
            tvDeliverTeam.setText("");
            tvDeliverName.setText("");
            tvDeliverPhone.setText("");
            llItemsContainer.removeAllViews();
            tvCustomerRemark.setText("");
            tvCsadRemark.setText("");
            llButtonContainer.setVisibility(View.INVISIBLE);
        }else{

            tvShopNo.setText(OrderHelper.getShopOrderSn(order));

            tvReachTime.setText(OrderHelper.getFormatTimeToStr(order.getExpectedTime()));
            
            long relationOrderId = order.getRelationOrderId();
            if(relationOrderId!=0){
                clReplenish.setVisibility(View.VISIBLE);
                tvRelationOrderId.setText(String.valueOf(relationOrderId));
                tvReason.setText(order.getReason());
            }else {
                clReplenish.setVisibility(View.GONE);
            }

            tvReceiverName.setText(order.getRecipient());
            tvReceiverPhone.setText(order.getPhone());
            ultIssueFeedback.setEnabled(true);
            tvOrderTime.setText(OrderHelper.getDateToString(order.getOrderTime()));
            tvOrderId.setText(String.valueOf(order.getId()));
            tvReceiverAddress.setText(order.getAddress());
            if (order.getDeliveryTeam() == DeliveryTeam.HAIKUI) {
                tvDeliverTeam.setText("(海葵)");
            } else if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
                tvDeliverTeam.setText("(美团)");
            } else if(order.getDeliveryTeam() == DeliveryTeam.ELE){
                tvDeliverTeam.setText("(饿了么)");
            } else {
                tvDeliverTeam.setText("(自有)");
            }

            tvDeliverName.setText(order.getCourierName());
            tvDeliverPhone.setText(order.getCourierPhone());
            updateCoffeeList(getContext(),llItemsContainer, order);
            tvCustomerRemark.setText(order.getNotes());
            tvCsadRemark.setText(order.getCsrNotes());

            if(order.getRevoked() || order.getStatus()>=OrderStatus.FINISHED){
                llButtonContainer.setVisibility(View.INVISIBLE);
            }else {
                llButtonContainer.setVisibility(View.VISIBLE);
                if (order.getProduceStatus() == OrderStatus.UNPRODUCED){
                    //待生产
                    llOneButton.setVisibility(VISIBLE);
                    llTwoButton.setVisibility(GONE);
                    if (OrderHelper.isTomorrowOrder(order)) {
                        btnProducePrint.setVisibility(View.GONE);
                    } else {
                        btnProducePrint.setVisibility(View.VISIBLE);
                    }
                }else if(order.getProduceStatus() == OrderStatus.PRODUCING){
                    //生产中
                    llOneButton.setVisibility(GONE);
                    llTwoButton.setVisibility(VISIBLE);
                    btnFinishProduce.setVisibility(View.VISIBLE);
                    if (OrderHelper.isPrinted(getContext(), order.getOrderSn())) {
                        btnPrint.setText(R.string.print_again);
                        btnPrint.setTextColor(getResources().getColor(R.color.red1));
                    } else {
                        btnPrint.setText(R.string.print);
                        btnPrint.setTextColor(getResources().getColor(R.color.white1));
                    }

                }else if(order.getProduceStatus() == OrderStatus.PRODUCED){
                    //已生产
                    llOneButton.setVisibility(GONE);
                    llTwoButton.setVisibility(VISIBLE);
                    btnFinishProduce.setVisibility(View.GONE);
                    if (OrderHelper.isPrinted(getContext(), order.getOrderSn())) {
                        btnPrint.setText(R.string.print_again);
                        btnPrint.setTextColor(getResources().getColor(R.color.text_red));
                    } else {
                        btnPrint.setText(R.string.print);
                        btnPrint.setTextColor(getResources().getColor(R.color.text_black));
                    }
                }
            }

        }
    }

    /**
     * 动态生成咖啡清单布局
     * @param llItemsContainer
     * @param order
     */
    private void updateCoffeeList(Context context,LinearLayout llItemsContainer, OrderBean order) {
        llItemsContainer.removeAllViews();
        if (order == null) {
            return;
        }

        List<ItemContentBean> items = order.getItems();
        for (ItemContentBean item : items) {
            TextView tv1 = new TextView(context);
            tv1.setId(R.id.item_name);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(9);
            tv1.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            tv1.setTextColor(context.getResources().getColor(R.color.black2));
            TextView tv2 = new TextView(context);
            tv2.setId(R.id.item_num);
            tv2.setText("x  " + item.getQuantity());
            tv2.getPaint().setFakeBoldText(true);
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            tv2.setTextColor(context.getResources().getColor(R.color.black2));

            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2, context);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2, context);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            String dingzhi = item.getRecipeFittings();
            if (!TextUtils.isEmpty(dingzhi)) {
                TextView tv5 = new TextView(context);
                tv5.setId(R.id.item_flag);
                tv5.setText(dingzhi);
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(), R.mipmap.flag_ding);
                drawable.setBounds(0, 1, OrderHelper.dip2Px(14, context), OrderHelper.dip2Px(14, context));
                tv5.setCompoundDrawablePadding(OrderHelper.dip2Px(4, context));
                tv5.setCompoundDrawables(drawable, null, null, null);
                tv5.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
                tv5.setTextColor(context.getResources().getColor(R.color.font_black));
                RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp5.addRule(RelativeLayout.BELOW, tv1.getId());
                lp5.addRule(RelativeLayout.ALIGN_LEFT, tv1.getId());
                tv5.setLayoutParams(lp5);
                rl.addView(tv5);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2, context);
            lp.bottomMargin = OrderHelper.dip2Px(4, context);
            llItemsContainer.addView(rl, lp);
        }

        TextView tv6 = new TextView(context);
        tv6.setText(context.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        tv6.setGravity(Gravity.CENTER);
        tv6.getPaint().setFakeBoldText(true);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp6.topMargin = OrderHelper.dip2Px(30, context);
        tv6.setLayoutParams(lp6);
        llItemsContainer.addView(tv6, lp6);

        llItemsContainer.invalidate();
    }


    /**
     * 响应点击事件
     */
    @Override
    public void onClick(View v) {
        if(mOrder==null){
            return;
        }
        switch (v.getId()) {
            case R.id.ult_issue_feedback:
                //问题反馈
               /* ReportIssueDialog rid = ReportIssueDialog.newInstance(mOrder.getId());
                rid.show(getContext().getChildFragmentManager(), "report_issue");*/
               if(callback!=null ){
                   callback.reportIssue(mOrder.getId());
               }
                break;
            case R.id.btn_finish_produce:
                //生产完成
                EventBus.getDefault().post(new FinishProduceEvent(mOrder));
                Logger.getLogger().log("详情-完成生产:{"+mOrder.getId()+"}");
                break;
            case R.id.btn_print:
                //打印
                EventBus.getDefault().post(new PrintOrderEvent(mOrder));
                Logger.getLogger().log("详情-打印:{"+mOrder.getId()+"}");
                break;
            case R.id.btn_produce_print:
                //开始生产兼打印
                EventBus.getDefault().post(new StartProduceEvent(mOrder,true));
                Logger.getLogger().log("详情-开始生产:{"+mOrder.getId()+"}");
                break;
        }
    }




    public interface ActionCallback {
        void reportIssue(long orderId);
    }
}
