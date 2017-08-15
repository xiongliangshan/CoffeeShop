package com.lyancafe.coffeeshop.produce.ui;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderCategory;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.NaiGaiEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.utils.OrderSortComparator;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ToProduceRvAdapter extends RecyclerView.Adapter<ToProduceRvAdapter.ViewHolder>{

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    public ListMode curMode;
    private List<OrderBean> batchOrders;
    private List<OrderBean> searchList;
    public Map<Integer,Boolean> selectMap;

    public ToProduceRvAdapter(Context context) {
        this.context = context;
        curMode = ListMode.NORMAL;
        batchOrders = new ArrayList<>();
        searchList = new ArrayList<>();
        selectMap = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final OrderBean order = list.get(position);

        if(curMode==ListMode.SELECT){
            holder.selectView.setVisibility(View.VISIBLE);
            holder.selectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!order.getWxScan()){
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                        selectMap.put(position,holder.checkBox.isChecked());
                    }

                }
            });
            holder.checkBox.setChecked(selectMap.get(position)==null?false:selectMap.get(position));
        }else {
            holder.selectView.setVisibility(View.GONE);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知详情板块内容变更
                selected = position;
                notifyDataSetChanged();
                if(position>=0 && position<list.size()){
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(position)));
                }

                Log.d(TAG, "点击了 " + position);
            }
        });

        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order_selected);
        }else{
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order);
        }

        holder.orderIdTxt.setText(OrderHelper.getShopOrderSn(order));

        //加急
        if("Y".equalsIgnoreCase(order.getReminder())){
            holder.reminderImg.setVisibility(View.VISIBLE);
            holder.reminderImg.setImageResource(R.mipmap.flag_reminder);
        }else{
            holder.reminderImg.setVisibility(View.GONE);
        }
        //扫码下单
        if(order.getWxScan()){
            holder.saoImg.setVisibility(View.VISIBLE);
            holder.saoImg.setImageResource(R.mipmap.flag_sao);
        }else {
            holder.saoImg.setVisibility(View.GONE);
        }
        //定制
        if(order.getIsRecipeFittings()){
            holder.labelFlagImg.setVisibility(View.VISIBLE);
            holder.labelFlagImg.setImageResource(R.mipmap.flag_ding);
        }else{
            holder.labelFlagImg.setVisibility(View.GONE);
        }

        //抢单
        if(order.getStatus()== OrderStatus.UNASSIGNED){
            holder.grabFlagIV.setVisibility(View.GONE);
        }else{
            holder.grabFlagIV.setVisibility(View.VISIBLE);
            holder.grabFlagIV.setImageResource(R.mipmap.flag_qiang);
        }
        //备注
        if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
            holder.remarkFlagIV.setVisibility(View.GONE);
        }else {
            holder.remarkFlagIV.setVisibility(View.VISIBLE);
            holder.remarkFlagIV.setImageResource(R.mipmap.flag_bei);
        }


        if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
            holder.expectedTimeText.setText(order.getInstant() == 1 ? "立即送出" : OrderHelper.getFormatTimeToStr(order.getExpectedTime()));
        } else {
            holder.expectedTimeText.setText(order.getInstant() == 1 ? "尽快送达" : OrderHelper.getFormatPeriodTimeStr(order.getExpectedTime()));
        }


        holder.deliverStatusText.setText(OrderHelper.getStatusName(order.getStatus(),order.getWxScan()));

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

        }else{
            holder.twobtnContainerLayout.setVisibility(View.VISIBLE);
            holder.onebtnContainerlayout.setVisibility(View.GONE);
            holder.produceBtn.setVisibility(View.GONE);

        }

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
            tv1.setTextColor(context.getResources().getColor(R.color.black2));
            if(!TextUtils.isEmpty(item.getRecipeFittings())){
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0,1,OrderHelper.dip2Px(12,context),OrderHelper.dip2Px(12,context));
                tv1.setCompoundDrawablePadding(OrderHelper.dip2Px(4,context));
                tv1.setCompoundDrawables(null, null,drawable,null);
            }
            TextView tv2 = new TextView(context);
            tv2.setText("x  " + item.getQuantity());
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            tv2.setTextColor(context.getResources().getColor(R.color.black2));
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

    public List<OrderBean> getBatchOrders() {
        List<OrderBean> batchOrders = new ArrayList<>();
        for(int i= 0;i<list.size();i++){
            Boolean isChecked = selectMap.get(i);
            if(isChecked!=null && isChecked.booleanValue()){
                batchOrders.add(list.get(i));
            }
        }
        return batchOrders;
    }


    //搜索
    public void searchOrder(final int shopOrderNo){
        Observable.fromIterable(list)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<OrderBean>() {
                    @Override
                    public boolean test(@NonNull OrderBean orderBean) throws Exception {
                        return orderBean.getShopOrderNo()==shopOrderNo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        searchList.clear();
                    }

                    @Override
                    public void onNext(@NonNull OrderBean orderBean) {
                        searchList.add(orderBean);
                        setData(searchList);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if(searchList.size()==0){
                            ToastUtil.show(context,"没有搜到目标订单");
                        }
                    }
                });

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_select_view) RelativeLayout selectView;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.root_view) LinearLayout rootLayout;
        @BindView(R.id.ll_first_row) LinearLayout firstRowLayout;
        @BindView(R.id.iv_reminder) ImageView reminderImg;
        @BindView(R.id.iv_sao_flag) ImageView saoImg;
        @BindView(R.id.iv_label_flag) ImageView labelFlagImg;
        @BindView(R.id.item_order_id) TextView orderIdTxt;
        @BindView(R.id.item_expected_time) TextView expectedTimeText;
        @BindView(R.id.item_grab_flag) ImageView grabFlagIV;
        @BindView(R.id.item_remark_flag) ImageView remarkFlagIV;
        @BindView(R.id.item_container) LinearLayout itemContainerll;
        @BindView(R.id.tv_deliver_status) TextView deliverStatusText;
        @BindView(R.id.tv_cup_count) TextView cupCountText;
        @BindView(R.id.ll_twobtn_container) LinearLayout twobtnContainerLayout;
        @BindView(R.id.ll_onebtn_container) LinearLayout onebtnContainerlayout;
        @BindView(R.id.item_produce_and_print) TextView produceAndPrintBtn;
        @BindView(R.id.item_produce) TextView produceBtn;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        Collections.sort(this.list,new OrderSortComparator());
        notifyDataSetChanged();
        EventBus.getDefault().post(new NaiGaiEvent(OrderHelper.caculateNaiGai(list)));
        if(selected>=0 && selected<this.list.size()){
            EventBus.getDefault().post(new UpdateOrderDetailEvent(this.list.get(selected)));
        }else{
            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
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
            EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(selected)));
        }else{
            selected = -1;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
        }
        //计算奶盖数量
        EventBus.getDefault().post(new NaiGaiEvent(OrderHelper.caculateNaiGai(list)));

    }

    /**
     * 点击批量开始生产，
     * @param orderIds
     */
    public void removeOrdersFromList(List<Long> orderIds){
        for(int i=list.size()-1;i>=0;i--){
            if(orderIds.contains(list.get(i).getId())){
                list.remove(i);
            }
        }

        if(list.size()>0){
            selected=0;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(selected)));
        }else{
            selected = -1;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
        }
        //计算奶盖数量
        EventBus.getDefault().post(new NaiGaiEvent(OrderHelper.caculateNaiGai(list)));
    }


}
