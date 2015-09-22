package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<OrderBean> list = new ArrayList<OrderBean>();
    private int selected = -1;

    public OrderGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item,null);
            holder  = new ViewHolder();
            holder.orderIdTxt = (TextView) convertView.findViewById(R.id.item_order_id);
            holder.contantEffectTimeTxt = (TextView) convertView.findViewById(R.id.item_contant_produce_effect);
            holder.effectTimeTxt = (TextView) convertView.findViewById(R.id.item_produce_effect);
            holder.grabFlagIV = (ImageView) convertView.findViewById(R.id.item_grab_flag);
            holder.remarkFlagIV = (ImageView) convertView.findViewById(R.id.item_remark_flag);
            holder.itemContainerll = (LinearLayout) convertView.findViewById(R.id.item_container);
            holder.produceBtn = (Button) convertView.findViewById(R.id.item_produce);
            holder.printBtn = (Button) convertView.findViewById(R.id.item_print);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final OrderBean order = list.get(position);
        holder.orderIdTxt.setText("CB2015-0912-15");
        holder.effectTimeTxt.setText("08:00");
        holder.grabFlagIV.setImageResource(R.mipmap.ic_launcher);
        holder.remarkFlagIV.setImageResource(R.mipmap.ic_launcher);
        return convertView;
    }

    class ViewHolder{
        TextView orderIdTxt;
        TextView contantEffectTimeTxt;
        TextView effectTimeTxt;
        ImageView grabFlagIV;
        ImageView remarkFlagIV;
        LinearLayout itemContainerll;
        Button produceBtn;
        Button printBtn;
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
