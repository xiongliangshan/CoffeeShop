package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.DeliverPlatform;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        createPlatformUI(holder.llPlatform,group.getDeliverPlatformMap());
    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_expected)
        TextView tvExpected;
        @BindView(R.id.ll_platform)
        LinearLayout llPlatform;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setData(List<SummarizeGroup> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    /**
     * 展示平台订单数据
     * @param container
     * @param dpMap
     */
    private void createPlatformUI(LinearLayout container, Map<String,DeliverPlatform> dpMap){
        Iterator<String> iterator = dpMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            DeliverPlatform dp = dpMap.get(key);
            if(dp.getOrderCount()>0){
                TextView textView = new TextView(mContext);
                textView.setText(dp.getName()+" : "+dp.getCupCount()+" 杯, "+dp.getOrderCount()+" 单");
                container.addView(textView);
            }
        }
    }
}
