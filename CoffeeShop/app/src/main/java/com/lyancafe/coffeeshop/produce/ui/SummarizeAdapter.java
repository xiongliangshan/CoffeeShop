package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/20.
 */

public class SummarizeAdapter extends RecyclerView.Adapter<SummarizeAdapter.ViewHolder> {


    private Context mContext;
    private List<SummarizeGroup> groupList;

    public SummarizeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.summarize_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SummarizeGroup group = groupList.get(position);
        holder.tvExpected.setText(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_expected)
        TextView tvExpected;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public void setData(List<SummarizeGroup> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }
}
