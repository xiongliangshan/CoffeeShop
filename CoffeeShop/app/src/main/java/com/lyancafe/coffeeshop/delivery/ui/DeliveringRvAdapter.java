package com.lyancafe.coffeeshop.delivery.ui;

import android.content.Context;
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

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderCategory;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.UpdateDeliverOrderDetailEvent;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/9/21.
 */
public class DeliveringRvAdapter extends RecyclerView.Adapter<DeliveringRvAdapter.ViewHolder>{

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;

    public DeliveringRvAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deliver_order_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知详情板块内容变更
                selected = position;
                notifyDataSetChanged();
                if(position>=0 && position<list.size()){
                    EventBus.getDefault().post(new UpdateDeliverOrderDetailEvent(list.get(position)));
                }

                Log.d(TAG, "点击了 " + position);
            }
        });

        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order_selected);
        }else{
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order);
        }
        final OrderBean order = list.get(position);

        /*if(order.getDeliveryTeam()== DeliveryTeam.MEITUAN){
            //美团订单
            holder.firstRowLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }else{
            holder.firstRowLayout.setBackground(null);
        }*/
        holder.orderIdTxt.setText(OrderHelper.getShopOrderSn(order));

        //新用户订单
        if(false){
            holder.newUserOderIV.setImageResource(R.mipmap.flag_new_user);
        }else{
            holder.newUserOderIV.setImageResource(R.mipmap.flag_placeholder);
        }
        //扫码下单
        if(order.isWxScan()){
            holder.saoImg.setImageResource(R.mipmap.flag_sao);
        }else {
            holder.saoImg.setImageResource(R.mipmap.flag_placeholder);
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
            holder.remarkFlagIV.setImageResource(R.mipmap.flag_bei);
        }
        //vip订单
        if(order.isOrderVip()){
            holder.vipFlagIV.setImageResource(R.mipmap.flag_vip);
        }else{
            holder.vipFlagIV.setImageResource(R.mipmap.flag_placeholder);
        }

        OrderHelper.showEffect(order, null, holder.effectTimeTxt);
        fillItemListData(holder.itemContainerll, order.getItems());
        if(order.getDeliveryTeam()==DeliveryTeam.MEITUAN){
            holder.bottomText.setText("美团配送");
        }else if(order.getDeliveryTeam()==DeliveryTeam.HAIKUI){
            holder.bottomText.setText("海葵配送");
        }else{
            holder.bottomText.setText(OrderHelper.getTimeToService(order));
        }

        holder.cupCountText.setText(context.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));

    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
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
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
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



    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view) LinearLayout rootLayout;
        @BindView(R.id.ll_first_row) LinearLayout firstRowLayout;
        @BindView(R.id.iv_new_user) ImageView newUserOderIV;
        @BindView(R.id.iv_sao_flag) ImageView saoImg;
        @BindView(R.id.iv_gift) ImageView giftIV;
        @BindView(R.id.iv_label_flag) ImageView labelFlagImg;
        @BindView(R.id.item_order_id) TextView orderIdTxt;
        @BindView(R.id.item_contant_produce_effect) TextView contantEffectTimeTxt;
        @BindView(R.id.item_produce_effect) TextView effectTimeTxt;
        @BindView(R.id.item_vip_flag) ImageView vipFlagIV;
        @BindView(R.id.item_grab_flag) ImageView grabFlagIV;
        @BindView(R.id.item_remark_flag) ImageView remarkFlagIV;
        @BindView(R.id.item_container) LinearLayout itemContainerll;
        @BindView(R.id.tv_cup_count) TextView cupCountText;
        @BindView(R.id.tv_item_bottom) TextView bottomText;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setData(List<OrderBean> list,int category){
        this.list = filterOrders(list,category);
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
            EventBus.getDefault().post(new UpdateDeliverOrderDetailEvent(this.list.get(selected)));
        }else{
            EventBus.getDefault().post(new UpdateDeliverOrderDetailEvent(null));
        }

    }

    private List<OrderBean> filterOrders(List<OrderBean> list,int category){
        List<OrderBean> subList = new ArrayList<>();
        if(category== OrderCategory.MEITUN){
            for(OrderBean orderBean:list){
                if(orderBean.getDeliveryTeam()==8){
                    subList.add(orderBean);
                }
            }
        }else if(category==OrderCategory.OWN){
            for(OrderBean orderBean:list){
                if(orderBean.getDeliveryTeam()!=8){
                    subList.add(orderBean);
                }
            }
        }else{
            subList = list;
        }

        return subList;
    }




    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param orderId
     */
    public void removeOrderFromList(long orderId){
        for(int i=list.size()-1;i>=0;i--){
            if(list.get(i).getId()==orderId){
                list.remove(i);
                break;
            }
        }
        if(list.size()>0){
            selected=0;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateDeliverOrderDetailEvent(list.get(selected)));
        }else{
            selected = -1;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateDeliverOrderDetailEvent(null));
        }


    }


}
