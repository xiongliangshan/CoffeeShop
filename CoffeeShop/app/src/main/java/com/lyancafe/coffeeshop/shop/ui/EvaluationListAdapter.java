package com.lyancafe.coffeeshop.shop.ui;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.EvaluationListSortComparator;
import com.lyancafe.coffeeshop.widget.EvaluationDetailDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EvaluationListAdapter extends RecyclerView.Adapter<EvaluationListAdapter.ViewHolder> {


    private Fragment mContext;
    public List<EvaluationBean> list = new ArrayList<>();

    public EvaluationListAdapter(Fragment mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluation_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EvaluationBean evaluationBean = list.get(position);
        holder.tvShopOrderId.setText(OrderHelper.getShopOrderSn(evaluationBean.getOrderbean()));
        String levelName=null;
        int levelColor = 0;
        switch (evaluationBean.getDeliveryParameter()) {
            case 1:
                levelName = "非常差";
                levelColor = mContext.getResources().getColor(R.color.red2);
                break;
            case 2:
                levelName = "很差";
                levelColor = mContext.getResources().getColor(R.color.red3);
                break;
            case 3:
                levelName = "一般";
                levelColor = mContext.getResources().getColor(R.color.yellow1);
                break;
            case 4:
                levelName = "很好";
                levelColor = mContext.getResources().getColor(R.color.green1);
                break;
            case 5:
                levelName = "非常好";
                levelColor = mContext.getResources().getColor(R.color.green2);
                break;
            default:
                levelName = "未知";
                break;
        }
        holder.tvLevelNameText.setText(levelName);
        holder.tvLevelNameText.setTextColor(levelColor);
        holder.flowLayout.setAdapter(new TagAdapter<String>(evaluationBean.getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_tv,parent,false);
                tv.setText(s);
                return tv;
            }
        });
        holder.flTasteLayout.setAdapter(new TagAdapter<String>(new ArrayList(evaluationBean.getProductTaste().keySet())) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_taste,parent,false);
                tv.setText(s+" : "+evaluationBean.getProductTaste().get(s)+"心");
                return tv;
            }
        });
        holder.detailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击查看详情
                EvaluationDetailDialog dialog = EvaluationDetailDialog.newInstance(evaluationBean.getOrderbean());
                dialog.show(mContext.getChildFragmentManager(),"evaluation_detail");
            }
        });
        holder.tvContentText.setText(evaluationBean.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_shopOrderId) TextView tvShopOrderId;
        @BindView(R.id.tv_level_name) TextView tvLevelNameText;
        @BindView(R.id.flowLayout) TagFlowLayout flowLayout;
        @BindView(R.id.fl_taste) TagFlowLayout flTasteLayout;
        @BindView(R.id.tv_content) TextView tvContentText;
        @BindView(R.id.tv_detail) TextView detailText;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setData(List<EvaluationBean> evaluationBeanList){
        this.list = evaluationBeanList;
        notifyDataSetChanged();
    }

    public void addData(List<EvaluationBean> list){
        this.list.addAll(list);
        Collections.sort(this.list,new EvaluationListSortComparator());
        notifyDataSetChanged();
    }
}
