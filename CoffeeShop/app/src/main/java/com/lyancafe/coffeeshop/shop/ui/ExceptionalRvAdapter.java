package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.event.UpdateExceptionalDetailEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ExceptionalRvAdapter extends RecyclerView.Adapter<ExceptionalRvAdapter.ViewHolder> {

    private static final String TAG = "OrderGridViewAdapter";
    private Context context;
    public List<ExceptionalOrder> list = new ArrayList<>();
    public int selected = -1;

    public ExceptionalRvAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exceptional_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知详情板块内容变更
                selected = position;
                notifyDataSetChanged();
                if (position >= 0 && position < list.size()) {
                    EventBus.getDefault().post(new UpdateExceptionalDetailEvent(list.get(position)));
                }

                Log.d(TAG, "点击了 " + position);
            }
        });

        if (selected == position) {
            holder.llRoot.setBackgroundResource(R.drawable.bg_finished_order_selected);
        } else {
            holder.llRoot.setBackgroundResource(R.drawable.bg_finished_order);
        }
        final ExceptionalOrder order = list.get(position);

        if (DeliveryTeam.MEITUAN == order.getDeliveryTeam()) {
            holder.tvShopOrderId.setText("美团"+order.getThirdShopOrderNo());
            holder.tvDeliverTeam.setText("美团配送");
        } else if (DeliveryTeam.HAIKUI == order.getDeliveryTeam()) {
            holder.tvShopOrderId.setText(OrderHelper.getShopOrderSn(order));
            holder.tvDeliverTeam.setText("海葵配送");
        } else {
            holder.tvShopOrderId.setText(OrderHelper.getShopOrderSn(order));
            holder.tvDeliverTeam.setText("配送团队"+order.getDeliveryTeam());
        }

        if(order.getAcceptOrPickup()==1){
            //未接
            holder.tvTimeoutType.setText("超时未接");
            holder.tvTimeOut.setText(order.getAcceptTimeOverInt()+"分钟");
            holder.llThirdInfo.setVisibility(View.INVISIBLE);
        }else if(order.getAcceptOrPickup()==2){
            //未取
            holder.tvTimeoutType.setText("超时未取");
            holder.tvTimeoutType.setBackgroundColor(CSApplication.getInstance().getResources().getColor(R.color.tab_orange));
            holder.tvTimeOut.setText(order.getAcceptOrPickup()+"分钟");
            holder.llThirdInfo.setVisibility(View.VISIBLE);
            holder.tvThirdPartyId.setText(order.getThirdOrderNo());
            holder.tvDeliverName.setText(order.getCourierName());
            holder.tvDeliverPhone.setText(order.getCourierPhone());
        }
        holder.tvOrderTime.setText(OrderHelper.formatOrderDate(order.getOrderTime()));
        holder.tvExpectedReachTime.setText(OrderHelper.getPeriodOfExpectedtime(order));


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_root)
        LinearLayout llRoot;
        @BindView(R.id.ll_third_info)
        LinearLayout llThirdInfo;
        @BindView(R.id.tv_shop_order_id)
        TextView tvShopOrderId;
        @BindView(R.id.tv_timeout_type)
        TextView tvTimeoutType;
        @BindView(R.id.tv_order_time)
        TextView tvOrderTime;
        @BindView(R.id.tv_expected_reach_time)
        TextView tvExpectedReachTime;
        @BindView(R.id.tv_time_out)
        TextView tvTimeOut;
        @BindView(R.id.tv_third_party_id)
        TextView tvThirdPartyId;
        @BindView(R.id.tv_deliver_name)
        TextView tvDeliverName;
        @BindView(R.id.tv_deliver_phone)
        TextView tvDeliverPhone;
        @BindView(R.id.tv_deliver_team)
        TextView tvDeliverTeam;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<ExceptionalOrder> list) {
        this.list = list;
        notifyDataSetChanged();
        if (selected >= 0 && selected < this.list.size()) {
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(this.list.get(selected)));
        } else {
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(null));
        }

    }

    public void addData(List<ExceptionalOrder> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
        if (selected >= 0 && selected < this.list.size()) {
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(this.list.get(selected)));
        } else {
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(null));
        }
    }


    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     *
     * @param orderId
     */
    public void removeOrderFromList(long orderId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getId() == orderId) {
                list.remove(i);
                break;
            }
        }
        if (list.size() > 0) {
            selected = 0;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(list.get(selected)));
        } else {
            selected = -1;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateExceptionalDetailEvent(null));
        }


    }


}
