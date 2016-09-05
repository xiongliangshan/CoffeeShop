package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class OrderListViewAdapter extends RecyclerView.Adapter<OrderListViewAdapter.ViewHolder>{


    private static final String TAG  ="OrderListViewAdapter";
    private List<SFGroupBean> groupList = new ArrayList<>();
    private Context mContext;
    public int selected = -1;
    private RecyclerView.LayoutManager mLayoutManager;

    public OrderListViewAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
    }

    @Override
    public OrderListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sf_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderListViewAdapter.ViewHolder holder, int position) {
        SFGroupBean sfGroupBean = groupList.get(position);
        holder.batchPromptText.setText(position + "条");
        holder.horizontalListView.setLayoutManager(mLayoutManager);
        holder.horizontalListView.setHasFixedSize(true);
        holder.horizontalListView.setItemAnimator(new DefaultItemAnimator());
        SFItemListAdapter adapter = new SFItemListAdapter(mContext,sfGroupBean.getOrderGroup());
        holder.horizontalListView.setAdapter(adapter);
    //    createItems(holder.sfItemContainerLayout,sfGroupBean);
    }

    private void createItems(LinearLayout sfItemContainerLayout, SFGroupBean sfGroupBean) {
        sfItemContainerLayout.removeAllViews();
        for(OrderBean orderBean:sfGroupBean.getOrderGroup()){
            View v = createItemView(orderBean);
            sfItemContainerLayout.addView(v);
        }
        sfItemContainerLayout.invalidate();
    }

    private View createItemView(final OrderBean order) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.order_list_item,null);

        RelativeLayout rootLayout = (RelativeLayout) itemView.findViewById(R.id.root_view);
        LinearLayout secondRootLayout = (LinearLayout) itemView.findViewById(R.id.second_root_view);
        ImageView giftIV = (ImageView) itemView.findViewById(R.id.iv_gift);
        ImageView labelFlagImg = (ImageView) itemView.findViewById(R.id.iv_label_flag);
        ImageView logoScanIV = (ImageView) itemView.findViewById(R.id.logo_scan);
        TextView orderIdTxt = (TextView) itemView.findViewById(R.id.item_order_id);
        TextView contantEffectTimeTxt = (TextView) itemView.findViewById(R.id.item_contant_produce_effect);
        TextView effectTimeTxt = (TextView) itemView.findViewById(R.id.item_produce_effect);
        ImageView issueFlagIV = (ImageView) itemView.findViewById(R.id.item_issue_flag);
        ImageView vipFlagIV = (ImageView) itemView.findViewById(R.id.item_vip_flag);
        ImageView grabFlagIV = (ImageView) itemView.findViewById(R.id.item_grab_flag);
        ImageView remarkFlagIV = (ImageView) itemView.findViewById(R.id.item_remark_flag);
        LinearLayout itemContainerll = (LinearLayout) itemView.findViewById(R.id.item_container);
        TextView cupCountText = (TextView) itemView.findViewById(R.id.tv_cup_count);
        LinearLayout twobtnContainerLayout = (LinearLayout) itemView.findViewById(R.id.ll_twobtn_container);
        LinearLayout onebtnContainerlayout = (LinearLayout) itemView.findViewById(R.id.ll_onebtn_container);
        TextView produceBtn = (TextView) itemView.findViewById(R.id.item_produce);
        TextView printBtn = (TextView) itemView.findViewById(R.id.item_print);
        TextView produceAndPrintBtn = (TextView) itemView.findViewById(R.id.item_produce_and_print);

        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(selected!=position && selected>-1){
                    notifyItemChanged(selected);
                    notifyItemChanged(position);
                    //通知详情板块内容变更
                    EventBus.getDefault().post(new UpdateOrderDetailEvent());
                }
                selected = position;
                Log.d(TAG, "点击了 " + position);*/
            }
        });

       /* if(selected==position){
            holder.rootLayout.setBackgroundResource(R.mipmap.touch_border);
        }else{
            holder.rootLayout.setBackground(null);
        }*/
        if(OrderHelper.isBatchOrder(order.getId())){
            secondRootLayout.setBackgroundResource(R.drawable.bg_batch_order);
            itemContainerll.setBackgroundResource(R.mipmap.bg_batch_dot);
        }else{
            secondRootLayout.setBackgroundResource(R.drawable.bg_order);
            itemContainerll.setBackgroundResource(R.mipmap.bg_normal_dot);
        }
        orderIdTxt.setText(OrderHelper.getShopOrderSn(order.getInstant(),order.getShopOrderNo()));
        if(order.isWxScan()){
            logoScanIV.setVisibility(View.VISIBLE);
        }else{
            logoScanIV.setVisibility(View.GONE);
        }
        //定制
        if(order.isRecipeFittings()){
            labelFlagImg.setImageResource(R.mipmap.flag_ding);
        }else{
            labelFlagImg.setImageResource(R.mipmap.flag_placeholder);
        }

        //礼盒订单 or 礼品卡
        if(order.getGift()==2||order.getGift()==5){
            giftIV.setImageResource(R.mipmap.flag_li);
        }else{
            giftIV.setImageResource(R.mipmap.flag_placeholder);
        }
        //抢单
        if(order.getStatus()== OrderStatus.UNASSIGNED){
            grabFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }else{
            grabFlagIV.setImageResource(R.mipmap.flag_qiang);
        }
        //备注
        if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
            remarkFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }else {
            remarkFlagIV.setImageResource(R.mipmap.flag_zhu);
        }
        //问题
        if(order.issueOrder()){
            issueFlagIV.setImageResource(R.mipmap.flag_issue);
        }else{
            issueFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }
        //vip订单
        if(order.isOrderVip()){
            vipFlagIV.setImageResource(R.mipmap.flag_vip);
        }else{
            vipFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }

        if(OrdersFragment.subTabIndex== TabList.TAB_TOPRODUCE){
            if(order.getInstant()==0){
                produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
            }else{
                produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            }
            OrderHelper.showEffectOnly(order,effectTimeTxt);
        }else{
            OrderHelper.showEffect(order, produceBtn, effectTimeTxt);
        }
        if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
            printBtn.setText(R.string.print_again);
            printBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
        }else{
            printBtn.setText(R.string.print);
            printBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
        }
        fillItemListData(itemContainerll, order.getItems());
        cupCountText.setText(mContext.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        if(OrdersFragment.subTabIndex == TabList.TAB_TOPRODUCE){
            twobtnContainerLayout.setVisibility(View.GONE);
            onebtnContainerlayout.setVisibility(View.VISIBLE);
            produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击开始生产（打印）按钮
                    EventBus.getDefault().post(new StartProduceEvent(order));
                }
            });
        }else{
            twobtnContainerLayout.setVisibility(View.VISIBLE);
            onebtnContainerlayout.setVisibility(View.GONE);
            if(OrdersFragment.subTabIndex!=TabList.TAB_PRODUCING){
                produceBtn.setEnabled(false);
            }else{
                produceBtn.setEnabled(true);
            }
            produceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //生产完成
                    EventBus.getDefault().post(new FinishProduceEvent(order));
                }
            });
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new PrintOrderEvent(order));
                }
            });
        }



        return itemView;
    }


    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(mContext);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(6);
            tv1.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            if(!TextUtils.isEmpty(OrderHelper.getLabelStr(item.getRecipeFittingsList()))){
                Drawable drawable = ContextCompat.getDrawable(CoffeeShopApplication.getInstance(), R.mipmap.flag_ding);
                drawable.setBounds(0,1,OrderHelper.dip2Px(12,mContext),OrderHelper.dip2Px(12,mContext));
                tv1.setCompoundDrawablePadding(OrderHelper.dip2Px(4,mContext));
                tv1.setCompoundDrawables(null, null,drawable,null);
            }
            TextView tv2 = new TextView(mContext);
            tv2.setText("x  " + item.getQuantity());
            tv2.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            TextPaint tp = tv2.getPaint();
            tp.setFakeBoldText(true);
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2,mContext);
            ll.addView(rl,lp);
        }
        ll.invalidate();
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public void setData(List<SFGroupBean> sfGroupList){
        this.groupList = sfGroupList;
        notifyDataSetChanged();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView batchPromptText;
        public Button batchHandlerBtn;
    //    public LinearLayout sfItemContainerLayout;
        public RecyclerView horizontalListView;

        public ViewHolder(View itemView) {
            super(itemView);
            batchPromptText = (TextView) itemView.findViewById(R.id.tv_sf_prompt);
            batchHandlerBtn = (Button) itemView.findViewById(R.id.btn_sf_handler);
        //    sfItemContainerLayout = (LinearLayout) itemView.findViewById(R.id.ll_sf_item_container);
            horizontalListView = (RecyclerView) itemView.findViewById(R.id.sf_horizontal_list);
        }
    }
}