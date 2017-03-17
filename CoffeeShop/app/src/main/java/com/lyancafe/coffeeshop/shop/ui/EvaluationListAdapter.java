package com.lyancafe.coffeeshop.shop.ui;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.widget.EvaluationDetailDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
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
        holder.tvShopOrderId.setText(OrderHelper.getShopOrderSn(evaluationBean));
        if(evaluationBean.getType()==4){
            //好评
            holder.tvEvalutaionType.setText("好评");
            holder.tvEvalutaionType.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        }else if(evaluationBean.getType()==5){
            //差评
            holder.tvEvalutaionType.setText("差评");
            holder.tvEvalutaionType.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        }
        holder.flowLayout.setAdapter(new TagAdapter<String>(evaluationBean.getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                /*TextView tv = new TextView(parent.getContext());
                tv.setTextColor(mContext.getResources().getColor(R.color.font_black));*/
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_tv,parent,false);
                tv.setText(s);
                return tv;
            }
        });
        holder.llDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击查看详情
                EvaluationDetailDialog dialog = EvaluationDetailDialog.newInstance(evaluationBean.getOrderId(),evaluationBean.getContent());
                dialog.show(mContext.getChildFragmentManager(),"evaluation_detail");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_shopOrderId) TextView tvShopOrderId;
        @BindView(R.id.tv_evalutaion_type) TextView tvEvalutaionType;
        @BindView(R.id.flowLayout) TagFlowLayout flowLayout;
        @BindView(R.id.ll_detail_layout) LinearLayout llDetailLayout;
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
        notifyDataSetChanged();
    }
}
