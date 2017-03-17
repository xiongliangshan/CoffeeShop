package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/3.
 */

public class TimeEffectListAdapter extends RecyclerView.Adapter<TimeEffectListAdapter.ViewHolder> {

    private Context mContext;
    public List<TimeEffectBean> list = new ArrayList<>();

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
        holder.tvOrderType.setText(timeEffectBean.getInstant()==1?"及时":"预约");
        holder.tvOrderTime.setText(OrderHelper.getDateToString(timeEffectBean.getOrderTime()));
        holder.tvExpectedReachTime.setText(OrderHelper.getDateToString(timeEffectBean.getExceptedTime()));
        holder.tvProducedTime.setText(OrderHelper.getDateToString(timeEffectBean.getProducedTime()));
        holder.tvGrabTime.setText(OrderHelper.getDateToString(timeEffectBean.getGrabTime()));
        holder.tvFetchTime.setText(OrderHelper.getDateToString(timeEffectBean.getFetchTime()));
        holder.tvRealReachTime.setText(OrderHelper.getDateToString(timeEffectBean.getDeliveredTime()));
        holder.tvDeliverName.setText(String.valueOf(timeEffectBean.getDeliverName()));
        switch (timeEffectBean.getLevleType()){
            case 1:
                holder.tvEffctDescription.setText("良好");
                holder.tvEffctDescription.setBackground(null);
                break;
            case 2:
                holder.tvEffctDescription.setText("合格");
                holder.tvEffctDescription.setBackground(null);
                break;
            case 3:
                holder.tvEffctDescription.setText("不及格");
                holder.tvEffctDescription.setBackgroundColor(mContext.getResources().getColor(R.color.font_red));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<TimeEffectBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(List<TimeEffectBean> list){
        this.list.addAll(list);
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
