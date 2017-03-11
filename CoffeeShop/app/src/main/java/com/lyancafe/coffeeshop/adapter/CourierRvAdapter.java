package com.lyancafe.coffeeshop.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.DeliverBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/11.
 */

public class CourierRvAdapter extends RecyclerView.Adapter<CourierRvAdapter.ViewHolder> {



    private List<DeliverBean> deliverBeanList;

    public CourierRvAdapter(List<DeliverBean> deliverBeanList) {
        this.deliverBeanList = deliverBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deliver_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeliverBean deliverBean = deliverBeanList.get(position);
        holder.tvDistance.setText("距离门店" +deliverBean.getDistanceToShop()+"米");
        holder.tvName.setText(deliverBean.getName());
        holder.tvOrderCount.setText(String.valueOf(deliverBean.getDeliveringOrderCount()));
        holder.tvPhone.setText(deliverBean.getPhone());
        holder.tvTotalOrderCount.setText(String.valueOf(deliverBean.getTotalOrderCount()));

    }

    @Override
    public int getItemCount() {
        return deliverBeanList == null ? 0 : deliverBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_phone) TextView tvPhone;
        @BindView(R.id.tv_distance) TextView tvDistance;
        @BindView(R.id.tv_total_order_count) TextView tvTotalOrderCount;
        @BindView(R.id.tv_order_count) TextView tvOrderCount;
        @BindView(R.id.tv_location) TextView tvLocation;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
