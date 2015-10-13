package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderGridViewAdapter extends BaseAdapter {

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    private List<OrderBean> list = new ArrayList<OrderBean>();
    private int selected = -1;

    public OrderGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        Log.d(TAG,"getCount "+list.size());
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
        Log.d(TAG,"getView 执行-------------------------"+list.get(position).getOrderSn());
        final ViewHolder holder ;
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
        holder.orderIdTxt.setText(order.getOrderSn());
        final long mms = order.getProduceEffect();
        holder.effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
        if(mms<=0){
            holder.effectTimeTxt.setText("已经超时");
        }else{
            order.setCDT(mms,holder.effectTimeTxt);
        }

        /*new CountDownTimer(mms,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = OrderHelper.getDateToMinutes(millisUntilFinished);
                holder.effectTimeTxt.setText(time);
                Log.d(TAG,millisUntilFinished+this.toString()+"--onTick");
            }

            @Override
            public void onFinish() {

            }
        }.start();*/


        holder.grabFlagIV.setImageResource(R.mipmap.ic_launcher);
        holder.remarkFlagIV.setImageResource(R.mipmap.ic_launcher);
        fillItemListData(holder.itemContainerll,order.getItems());
        return convertView;
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            TextView tv2 = new TextView(context);
            tv2.setText("X " + item.getQuantity());
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(5,context);;
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(5,context);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ll.addView(rl,lp);
        }
        ll.invalidate();
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
