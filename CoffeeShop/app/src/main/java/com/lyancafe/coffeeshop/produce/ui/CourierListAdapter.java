package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.produce.model.CourierBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class CourierListAdapter extends BaseAdapter{

    private Context mContext;
    private List<CourierBean> courierList = new ArrayList<>();

    public CourierListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
       return courierList.size();
    }

    @Override
    public Object getItem(int position) {
        return courierList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourierViewHolder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_list_item,null);
            holder = new CourierViewHolder();
            holder.courierNameText = (TextView) convertView.findViewById(R.id.courier_name);
            holder.courierJobText = (TextView) convertView.findViewById(R.id.courier_job);
            holder.courierDistanceText = (TextView) convertView.findViewById(R.id.courier_distance);
            holder.courierOrderCountText = (TextView) convertView.findViewById(R.id.courier_order_count);
            convertView.setTag(holder);
        }else{
            holder = (CourierViewHolder) convertView.getTag();
        }
        CourierBean courierBean = courierList.get(position);
        holder.courierNameText.setText(courierBean.getName());
        if(courierBean.getType()==1){
            holder.courierJobText.setText("(全职)");
        }else if(courierBean.getType()==2){
            holder.courierJobText.setText("(兼职)");
        }else{
            holder.courierJobText.setText("(其他)");
        }
        if(courierBean.getDistance()==-1){
            holder.courierDistanceText.setText("未知");
        }else{
            holder.courierDistanceText.setText(OrderHelper.getDistanceFormat(courierBean.getDistance()));
        }
        holder.courierOrderCountText.setText(courierBean.getOrderCount()+"单未完成");
        return convertView;
    }


    public void setData(List<CourierBean> list){
        this.courierList = list;
        notifyDataSetChanged();
    }


    public static class CourierViewHolder{
        public TextView courierNameText;
        public TextView courierJobText;
        public TextView courierDistanceText;
        public TextView courierOrderCountText;
    }
}
