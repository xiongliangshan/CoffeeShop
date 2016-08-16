package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderGridViewAdapter extends BaseAdapter{

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    private OrdersFragment orderFragment;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    public Timer timer;
    private TimerTask timerTask;
    private final static long DELTA_TIME = 30*1000;//单位ms
    public static  ArrayList<OrderBean> testList =  new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheToProduceList =  new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheProducingList = new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheProducedList =  new ArrayList<OrderBean>();
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(OrdersFragment.subTabIndex== TabList.TAB_TOPRODUCE){
                        refreshTimerData(cacheToProduceList);
                    }
                    break;
            }

        }
    };

    public OrderGridViewAdapter(Context context,Fragment fragment) {
        this.context = context;
        timer =  new Timer(true);
        this.orderFragment = (OrdersFragment)fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item,null);
            holder  = new ViewHolder();
            holder.rootLayout = (RelativeLayout) convertView.findViewById(R.id.root_view);
            holder.secondRootLayout = (LinearLayout) convertView.findViewById(R.id.second_root_view);
            holder.giftIV = (ImageView) convertView.findViewById(R.id.iv_gift);
            holder.labelFlagImg = (ImageView) convertView.findViewById(R.id.iv_label_flag);
            holder.logoScanIV = (ImageView) convertView.findViewById(R.id.logo_scan);
            holder.orderIdTxt = (TextView) convertView.findViewById(R.id.item_order_id);
            holder.contantEffectTimeTxt = (TextView) convertView.findViewById(R.id.item_contant_produce_effect);
            holder.effectTimeTxt = (TextView) convertView.findViewById(R.id.item_produce_effect);
            holder.issueFlagIV = (ImageView) convertView.findViewById(R.id.item_issue_flag);
            holder.vipFlagIV = (ImageView) convertView.findViewById(R.id.item_vip_flag);
            holder.grabFlagIV = (ImageView) convertView.findViewById(R.id.item_grab_flag);
            holder.remarkFlagIV = (ImageView) convertView.findViewById(R.id.item_remark_flag);
            holder.itemContainerll = (LinearLayout) convertView.findViewById(R.id.item_container);
            holder.cupCountText = (TextView) convertView.findViewById(R.id.tv_cup_count);
            holder.twobtnContainerLayout = (LinearLayout) convertView.findViewById(R.id.ll_twobtn_container);
            holder.onebtnContainerlayout = (LinearLayout) convertView.findViewById(R.id.ll_onebtn_container);
            holder.produceBtn = (TextView) convertView.findViewById(R.id.item_produce);
            holder.printBtn = (TextView) convertView.findViewById(R.id.item_print);
            holder.produceAndPrintBtn = (TextView) convertView.findViewById(R.id.item_produce_and_print);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        if(selected==position){
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

        if(OrdersFragment.subTabIndex==TabList.TAB_TOPRODUCE){
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
        if(OrdersFragment.subTabIndex == TabList.TAB_TOPRODUCE){
            holder.twobtnContainerLayout.setVisibility(View.GONE);
            holder.onebtnContainerlayout.setVisibility(View.VISIBLE);
            holder.produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击开始生产（打印）按钮
                    EventBus.getDefault().post(new StartProduceEvent(order));
                }
            });
        }else{
            holder.twobtnContainerLayout.setVisibility(View.VISIBLE);
            holder.onebtnContainerlayout.setVisibility(View.GONE);
            if(OrdersFragment.subTabIndex!=TabList.TAB_PRODUCING){
                holder.produceBtn.setEnabled(false);
            }else{
                holder.produceBtn.setEnabled(true);
            }
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
                //    orderFragment.printOrder(context,order);
                    EventBus.getDefault().post(new PrintOrderEvent(order));
                }
            });
        }


        return convertView;
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



    static class ViewHolder{

        RelativeLayout rootLayout;
        LinearLayout secondRootLayout;
        ImageView giftIV;
        ImageView labelFlagImg;
        ImageView logoScanIV;
        TextView orderIdTxt;
        TextView contantEffectTimeTxt;
        TextView effectTimeTxt;
        ImageView issueFlagIV;
        ImageView vipFlagIV;
        ImageView grabFlagIV;
        ImageView remarkFlagIV;
        LinearLayout itemContainerll;
        TextView cupCountText;
        LinearLayout twobtnContainerLayout;
        LinearLayout onebtnContainerlayout;
        TextView produceAndPrintBtn;
        TextView produceBtn;
        TextView printBtn;
    }

    public void setData(List<OrderBean> list){
        this.list = list;
    //    selected = 0;
        notifyDataSetChanged();
        if(OrdersFragment.subTabIndex==TabList.TAB_TOPRODUCE){
            //缓存订单列表
            cacheToProduceList.clear();
            cacheToProduceList.addAll(list);
            testList = (ArrayList<OrderBean>) cacheToProduceList.clone();
            if(timerTask==null){
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG,"timerTask run ---"+Thread.currentThread().getId());
                        for(OrderBean order:cacheToProduceList){
                            order.setProduceEffect(order.getProduceEffect()-DELTA_TIME);
                        }
                        handler.sendEmptyMessage(1);
                    }
                };
                timer.schedule(timerTask,DELTA_TIME,DELTA_TIME);
            }

        }else if(OrdersFragment.subTabIndex==TabList.TAB_PRODUCING){
            //缓存订单列表
            cacheProducingList.clear();
            cacheProducingList.addAll(list);

        } else if(OrdersFragment.subTabIndex==TabList.TAB_PRODUCED){
            cacheProducedList.clear();
            cacheProducedList.addAll(list);
        }




    }

    private void setFlag(OrderBean orderBean){

    }

    private void refreshTimerData(List<OrderBean> order_list){
        this.list = order_list;
        notifyDataSetChanged();
    }


    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param orderId
     * @param list
     */
    public void removeOrderFromList(long orderId,List<OrderBean> list){
        for(int i = 0;i<list.size();i++){
            if(list.get(i).getId()==orderId){
                list.remove(i);
                this.list = list;
                selected=0;
                notifyDataSetChanged();
                break;
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //通知详情板块内容变更
        EventBus.getDefault().post(new UpdateOrderDetailEvent());
    }
}
