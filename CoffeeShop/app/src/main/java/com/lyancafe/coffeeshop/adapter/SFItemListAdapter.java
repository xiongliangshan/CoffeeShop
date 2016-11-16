package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SFItemListAdapter extends RecyclerView.Adapter<SFItemListAdapter.ViewHolder> {

    private static String TAG ="SFItemListAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();


    public SFItemListAdapter(Context context, List<OrderBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item,parent,false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(v.getLayoutParams());
        lp.width = OrderHelper.dip2Px(180, context);
        lp.rightMargin = OrderHelper.dip2Px(8, context);
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(position)));
                OrderListViewAdapter.selectedOrderId = list.get(position).getId();
                Log.d(TAG, "点击了 " + position);
            }
        });

        if(OrderListViewAdapter.selectedOrderId==list.get(position).getId()){
            holder.rootLayout.setBackgroundResource(R.mipmap.touch_border);
        }else{
            holder.rootLayout.setBackground(null);
        }
        final OrderBean order = list.get(position);
        if(OrderHelper.isBatchOrder(order.getId())){
            holder.secondRootLayout.setBackgroundResource(R.drawable.bg_batch_order);
            holder.itemContainerll.setBackgroundResource(R.mipmap.bg_batch_dot);
        }else{
            holder.secondRootLayout.setBackgroundResource(R.drawable.bg_order);
            holder.itemContainerll.setBackgroundResource(R.mipmap.bg_normal_dot);
        }
        holder.orderIdTxt.setText(OrderHelper.getShopOrderSn(order.getInstant(),order.getShopOrderNo()));
        if(order.isWxScan()){
            holder.logoScanIV.setVisibility(View.VISIBLE);
        }else{
            holder.logoScanIV.setVisibility(View.GONE);
        }
        //新用户订单
        if(order.isNewCBUser()){
            holder.newUserOderIV.setImageResource(R.mipmap.flag_new_user);
        }else{
            holder.newUserOderIV.setImageResource(R.mipmap.flag_placeholder);
        }

        //定制
        if(order.isRecipeFittings()){
            holder.labelFlagImg.setImageResource(R.mipmap.flag_ding);
        }else{
            holder.labelFlagImg.setImageResource(R.mipmap.flag_placeholder);
        }

        //礼盒订单 or 礼品卡
        if(order.getGift()==2||order.getGift()==5){
            holder.giftIV.setImageResource(R.mipmap.flag_li);
        }else{
            holder.giftIV.setImageResource(R.mipmap.flag_placeholder);
        }
        //抢单
        if(order.getStatus()== OrderStatus.UNASSIGNED){
            holder.grabFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }else{
            holder.grabFlagIV.setImageResource(R.mipmap.flag_qiang);
        }
        //备注
        if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
            holder.remarkFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }else {
            holder.remarkFlagIV.setImageResource(R.mipmap.flag_zhu);
        }
        //问题
        if(order.issueOrder()){
            holder.issueFlagIV.setImageResource(R.mipmap.flag_issue);
        }else{
            holder.issueFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }
        //vip订单
        if(order.isOrderVip()){
            holder.vipFlagIV.setImageResource(R.mipmap.flag_vip);
        }else{
            holder.vipFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }

        if(order.getProduceStatus()== OrderStatus.UNPRODUCED){
            if(order.getInstant()==0){
                holder.produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
            }else{
                holder.produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            }
            OrderHelper.showEffectOnly(order,holder.effectTimeTxt);
        }else{
            OrderHelper.showEffect(order, holder.produceBtn, holder.effectTimeTxt);
        }
        if(OrderHelper.isPrinted(context, order.getOrderSn())){
            holder.printBtn.setText(R.string.print_again);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_red));
        }else{
            holder.printBtn.setText(R.string.print);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_black));
        }
        fillItemListData(holder.itemContainerll, order.getItems());
        holder.cupCountText.setText(context.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        if(order.getProduceStatus() == OrderStatus.UNPRODUCED){
            holder.twobtnContainerLayout.setVisibility(View.GONE);
            holder.onebtnContainerlayout.setVisibility(View.VISIBLE);
            holder.produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击开始生产（打印）按钮
                    EventBus.getDefault().post(new StartProduceEvent(order));
                }
            });
        }else if(order.getProduceStatus() == OrderStatus.PRODUCING){
            holder.twobtnContainerLayout.setVisibility(View.VISIBLE);
            holder.onebtnContainerlayout.setVisibility(View.GONE);
            holder.produceBtn.setVisibility(View.VISIBLE);
            holder.produceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //生产完成
                    EventBus.getDefault().post(new FinishProduceEvent(order));
                }
            });
            holder.printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new PrintOrderEvent(order));
                }
            });
        }else{
            holder.twobtnContainerLayout.setVisibility(View.VISIBLE);
            holder.onebtnContainerlayout.setVisibility(View.GONE);
            holder.produceBtn.setVisibility(View.GONE);
            holder.printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new PrintOrderEvent(order));
                }
            });
        }
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(6);
            tv1.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            if(!TextUtils.isEmpty(OrderHelper.getLabelStr(item.getRecipeFittingsList()))){
                Drawable drawable = ContextCompat.getDrawable(CoffeeShopApplication.getInstance(), R.mipmap.flag_ding);
                drawable.setBounds(0,1,OrderHelper.dip2Px(12,context),OrderHelper.dip2Px(12,context));
                tv1.setCompoundDrawablePadding(OrderHelper.dip2Px(4,context));
                tv1.setCompoundDrawables(null, null,drawable,null);
            }
            TextView tv2 = new TextView(context);
            tv2.setText("x  " + item.getQuantity());
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            TextPaint tp = tv2.getPaint();
            tp.setFakeBoldText(true);
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2,context);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2,context);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2,context);
            ll.addView(rl,lp);
        }
        ll.invalidate();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout rootLayout;
        public LinearLayout secondRootLayout;
        public ImageView newUserOderIV;
        public ImageView giftIV;
        public ImageView labelFlagImg;
        public ImageView logoScanIV;
        public TextView orderIdTxt;
        public TextView contantEffectTimeTxt;
        public TextView effectTimeTxt;
        public ImageView issueFlagIV;
        public ImageView vipFlagIV;
        public ImageView grabFlagIV;
        public ImageView remarkFlagIV;
        public LinearLayout itemContainerll;
        public TextView cupCountText;
        public LinearLayout twobtnContainerLayout;
        public LinearLayout onebtnContainerlayout;
        public TextView produceAndPrintBtn;
        public TextView produceBtn;
        public TextView printBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.root_view);
            secondRootLayout = (LinearLayout) itemView.findViewById(R.id.second_root_view);
            newUserOderIV = (ImageView) itemView.findViewById(R.id.iv_new_user);
            giftIV = (ImageView) itemView.findViewById(R.id.iv_gift);
            labelFlagImg = (ImageView) itemView.findViewById(R.id.iv_label_flag);
            logoScanIV = (ImageView) itemView.findViewById(R.id.logo_scan);
            orderIdTxt = (TextView) itemView.findViewById(R.id.item_order_id);
            contantEffectTimeTxt = (TextView) itemView.findViewById(R.id.item_contant_produce_effect);
            effectTimeTxt = (TextView) itemView.findViewById(R.id.item_produce_effect);
            issueFlagIV = (ImageView) itemView.findViewById(R.id.item_issue_flag);
            vipFlagIV = (ImageView) itemView.findViewById(R.id.item_vip_flag);
            grabFlagIV = (ImageView) itemView.findViewById(R.id.item_grab_flag);
            remarkFlagIV = (ImageView) itemView.findViewById(R.id.item_remark_flag);
            itemContainerll = (LinearLayout) itemView.findViewById(R.id.item_container);
            cupCountText = (TextView) itemView.findViewById(R.id.tv_cup_count);
            twobtnContainerLayout = (LinearLayout) itemView.findViewById(R.id.ll_twobtn_container);
            onebtnContainerlayout = (LinearLayout) itemView.findViewById(R.id.ll_onebtn_container);
            produceBtn = (TextView) itemView.findViewById(R.id.item_produce);
            printBtn = (TextView) itemView.findViewById(R.id.item_print);
            produceAndPrintBtn = (TextView) itemView.findViewById(R.id.item_produce_and_print);
        }
    }
}
