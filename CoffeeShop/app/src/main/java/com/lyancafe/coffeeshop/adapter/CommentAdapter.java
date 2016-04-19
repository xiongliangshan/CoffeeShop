package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.event.ClickCommentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/4/13.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<OrderBean> orderList = new ArrayList<>();

    public CommentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(contentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrderBean orderBean = orderList.get(position);
        holder.indexText.setText((position+1)+".");
        holder.orderSnText.setText(orderBean.getOrderSn());
        holder.labelText.setText(orderBean.getFeedbackTags().get(0));
        holder.itemRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ClickCommentEvent(orderBean));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setData(List<OrderBean> orderList){
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemRootLayout;
        public TextView indexText;
        public TextView orderSnText;
        public TextView labelText;
        public ViewHolder(View itemView) {
            super(itemView);
            itemRootLayout = (RelativeLayout) itemView.findViewById(R.id.rl_item_root);
            indexText = (TextView) itemView.findViewById(R.id.tv_index);
            orderSnText = (TextView) itemView.findViewById(R.id.tv_order_sn);
            labelText = (TextView) itemView.findViewById(R.id.tv_label);
        }
    }
}
