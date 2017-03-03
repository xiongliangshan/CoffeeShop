package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/3.
 */

public class TimeEffectListAdapter extends RecyclerView.Adapter<TimeEffectListAdapter.ViewHolder> {

    private Context mContext;
    private List<TimeEffectBean> list = new ArrayList<>();

    public TimeEffectListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_effect_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TimeEffectBean timeEffectBean = list.get(position);
        holder.tvShopNo.setText(String.valueOf(timeEffectBean.getShopOrderNo()));
        holder.tvOrderId.setText(String.valueOf(timeEffectBean.getOrderId()));
        holder.tvOrderType.setText(String.valueOf(timeEffectBean.getInstant()));
        holder.tvOrderTime.setText(String.valueOf(timeEffectBean.getOrderTime()));
        holder.tvExpectedReachTime.setText(String.valueOf(timeEffectBean.getExceptedTime()));
        holder.tvProducedTime.setText(String.valueOf(timeEffectBean.getProducedTime()));
        holder.tvGrabTime.setText(String.valueOf(timeEffectBean.getGrabTime()));
        holder.tvFetchTime.setText(String.valueOf(timeEffectBean.getFetchTime()));
        holder.tvRealReachTime.setText(String.valueOf(timeEffectBean.getDeliveredTime()));
        holder.tvDeliverName.setText(String.valueOf(timeEffectBean.getDeliverName()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<TimeEffectBean> list){
        this.list = list;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_effct_description) TextView tvEffctDescription;
        @BindView(R.id.tv_shop_no) TextView tvShopNo;
        @BindView(R.id.tv_order_id) TextView tvOrderId;
        @BindView(R.id.tv_order_type) TextView tvOrderType;
        @BindView(R.id.tv_order_time) TextView tvOrderTime;
        @BindView(R.id.tv_expected_reach_time) TextView tvExpectedReachTime;
        @BindView(R.id.tv_produced_time) TextView tvProducedTime;
        @BindView(R.id.tv_grab_time) TextView tvGrabTime;
        @BindView(R.id.tv_fetch_time) TextView tvFetchTime;
        @BindView(R.id.tv_real_reach_time) TextView tvRealReachTime;
        @BindView(R.id.tv_deliver_name) TextView tvDeliverName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
