package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/10.
 */
public class QueryListRecyclerAdapter extends RecyclerView.Adapter<QueryListRecyclerAdapter.ViewHolder> {

    private static final String TAG ="QueryRecyclerAdapter";
    public List<OrderBean> itemList = new ArrayList<OrderBean>();
    private Context context;
    private OrderQueryFragment fragment;
    public int selected = -1;

    public QueryListRecyclerAdapter(List<OrderBean> itemList, Context context,OrderQueryFragment fragment) {
        this.itemList = itemList;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected!=position && selected>-1){
                    notifyItemChanged(selected);
                    notifyItemChanged(position);
                }
                notifyDataSetChanged();
                fragment.updateDetailView(itemList.get(position));
                selected = position;
                Log.d(TAG, "点击了 " + position);
            }
        });
        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.mipmap.touch_border);
        }else{
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order);
        }
        final OrderBean order = itemList.get(position);
        if(order.isWxScan()){
            holder.logoScanIV.setVisibility(View.INVISIBLE);
        }else{
            holder.logoScanIV.setVisibility(View.GONE);
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

        holder.orderIdTxt.setText(OrderHelper.getShopOrderSn(order.getInstant(), order.getShopOrderNo()));
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);

        if(OrdersFragment.subTabIndex== TabList.TAB_TOPRODUCE){
            holder.produceBtn.setEnabled(true);

            if(mms<=0){
                holder.effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
                holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
                holder.effectTimeTxt.setText("+"+ OrderHelper.getDateToMinutes(Math.abs(mms)));
            }else{
                if(order.getInstant()==0){
                    holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                }else{
                    holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                }
                holder.effectTimeTxt.setTextColor(Color.parseColor("#000000"));
                holder.effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
            }

        }else{
            holder.produceBtn.setEnabled(false);
            holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            holder.effectTimeTxt.setTextColor(Color.parseColor("#000000"));
            holder.effectTimeTxt.setText("-----");
        }
        if(OrderHelper.isPrinted(context,order.getOrderSn())){
            holder.printBtn.setText(R.string.print_again);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_red));
        }else{
            holder.printBtn.setText(R.string.print);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_black));
        }

        fillItemListData(holder.itemContainerll, order.getItems());
        holder.cupCountText.setText(context.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        holder.produceBtn.setEnabled(false);
        /*holder.produceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生产完成
                ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener(){
                    @Override
                    public void onClickYes() {
                        Log.d(TAG,"postion = "+position+",orderId = "+order.getOrderSn());
                //         new OrderGridViewAdapter.DoFinishProduceQry(order.getId()).doRequest();
                    }
                });
                grabConfirmDialog.setContent("订单 "+order.getOrderSn()+" 生产完成？");
                grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                grabConfirmDialog.show();

            }
        });*/
        /*holder.printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrintOrderActivity.class);
                intent.putExtra("order",order);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public void setData(List<OrderBean> list){
        this.itemList = list;
        notifyDataSetChanged();
        selected = 0;
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
                Drawable drawable = ContextCompat.getDrawable(CoffeeShopApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0,1,OrderHelper.dip2Px(12,context),OrderHelper.dip2Px(12,context));
                tv1.setCompoundDrawablePadding(OrderHelper.dip2Px(4,context));
                tv1.setCompoundDrawables(null, null,drawable,null);
            }
            TextView tv2 = new TextView(context);
            tv2.setText("x " + item.getQuantity());
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            TextPaint tp = tv2.getPaint();
            tp.setFakeBoldText(true);
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(5,context);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(5,context);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4,context);
            ll.addView(rl,lp);
        }
        ll.invalidate();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rootLayout;
        public ImageView logoScanIV;
        public ImageView giftIV;
        public ImageView labelFlagImg;
        public ImageView issueFlagIV;
        public ImageView grabFlagIV;
        public ImageView remarkFlagIV;
        public TextView orderIdTxt;
        public TextView contantEffectTimeTxt;
        public TextView effectTimeTxt;
        public LinearLayout itemContainerll;
        public TextView cupCountText;
        public TextView produceBtn;
        public TextView printBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.root_view);
            logoScanIV = (ImageView) itemView.findViewById(R.id.logo_scan);
            giftIV = (ImageView) itemView.findViewById(R.id.iv_gift);
            labelFlagImg = (ImageView) itemView.findViewById(R.id.iv_label_flag);
            orderIdTxt = (TextView) itemView.findViewById(R.id.item_order_id);
            contantEffectTimeTxt = (TextView) itemView.findViewById(R.id.item_contant_produce_effect);
            effectTimeTxt = (TextView) itemView.findViewById(R.id.item_produce_effect);
            issueFlagIV = (ImageView) itemView.findViewById(R.id.item_issue_flag);
            grabFlagIV = (ImageView) itemView.findViewById(R.id.item_grab_flag);
            remarkFlagIV = (ImageView) itemView.findViewById(R.id.item_remark_flag);
            itemContainerll = (LinearLayout) itemView.findViewById(R.id.item_container);
            cupCountText = (TextView) itemView.findViewById(R.id.tv_cup_count);
            produceBtn = (TextView) itemView.findViewById(R.id.item_produce);
            printBtn = (TextView) itemView.findViewById(R.id.item_print);
        }
    }
}
