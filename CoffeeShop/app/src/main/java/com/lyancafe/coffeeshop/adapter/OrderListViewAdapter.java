package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class OrderListViewAdapter extends RecyclerView.Adapter<OrderListViewAdapter.ViewHolder>{


    private static final String TAG  ="OrderListViewAdapter";
    public List<SFGroupBean> groupList = new ArrayList<>();
    private Context mContext;
    public static long selectedOrderId = 0;

    public OrderListViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public OrderListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sf_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderListViewAdapter.ViewHolder holder, int position) {
        SFGroupBean sfGroupBean = groupList.get(position);
        holder.batchPromptText.setText(position + "条");
        holder.horizontalListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalListView.setHasFixedSize(true);
        holder.horizontalListView.setItemAnimator(new DefaultItemAnimator());
        SFItemListAdapter adapter = new SFItemListAdapter(mContext,sfGroupBean.getItemGroup());
        holder.horizontalListView.setAdapter(adapter);
    }



    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public void setData(List<SFGroupBean> sfGroupList){
        this.groupList = sfGroupList;
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView batchPromptText;
        public Button batchHandlerBtn;
        public RecyclerView horizontalListView;

        public ViewHolder(View itemView) {
            super(itemView);
            batchPromptText = (TextView) itemView.findViewById(R.id.tv_sf_prompt);
            batchHandlerBtn = (Button) itemView.findViewById(R.id.btn_sf_handler);
            horizontalListView = (RecyclerView) itemView.findViewById(R.id.sf_horizontal_list);
        }
    }


    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param orderId 顺风单组中被操作的订单Id
     */
    public void changeAndRemoveOrderFromList(long orderId,int produceStatus){
        for(int i=groupList.size()-1;i>=0;i--){
            for(OrderBean orderBean:groupList.get(i).getItemGroup()){
                if(orderId==orderBean.getId()){
                    orderBean.setProduceStatus(produceStatus);
                    if(OrderHelper.isSameStatus(groupList.get(i),produceStatus)){
                        int changeOrderSize = groupList.get(i).getItemGroup().size();
                        groupList.remove(i);
                        switch (produceStatus){
                            case OrderStatus.PRODUCING:
                                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE,changeOrderSize));
                                break;
                            case OrderStatus.PRODUCED:
                                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE,changeOrderSize));
                                break;
                        }

                    }
                    notifyDataSetChanged();
                    return;
                }
            }
        }

    }



}