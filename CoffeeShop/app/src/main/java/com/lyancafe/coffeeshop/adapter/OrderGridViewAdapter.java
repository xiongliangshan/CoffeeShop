package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderGridViewAdapter extends BaseAdapter {

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    public Timer timer;
    private TimerTask timerTask;
    private final static long DELTA_TIME = 300*1000;//单位ms
    private ArrayList<OrderBean> order_list =  new ArrayList<OrderBean>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    refreshTimerData(order_list);
                    break;
            }

        }
    };

    public OrderGridViewAdapter(Context context) {
        this.context = context;
        timer =  new Timer(true);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG,"getView 执行---------"+position+"---------------"+list.get(position).getOrderSn());
        final ViewHolder holder ;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item,null);
            holder  = new ViewHolder();
            holder.rootLayout = (LinearLayout) convertView.findViewById(R.id.root_view);
            holder.orderIdTxt = (TextView) convertView.findViewById(R.id.item_order_id);
            holder.contantEffectTimeTxt = (TextView) convertView.findViewById(R.id.item_contant_produce_effect);
            holder.effectTimeTxt = (TextView) convertView.findViewById(R.id.item_produce_effect);
            holder.grabFlagIV = (ImageView) convertView.findViewById(R.id.item_grab_flag);
            holder.remarkFlagIV = (ImageView) convertView.findViewById(R.id.item_remark_flag);
            holder.itemContainerll = (LinearLayout) convertView.findViewById(R.id.item_container);
            holder.produceBtn = (TextView) convertView.findViewById(R.id.item_produce);
            holder.printBtn = (TextView) convertView.findViewById(R.id.item_print);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.drawable.order_item_stroke_background);
        }else{
            holder.rootLayout.setBackground(null);
        }
        final OrderBean order = list.get(position);
        holder.orderIdTxt.setText(order.getOrderSn());
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);
        if(mms<=0){
            holder.effectTimeTxt.setText("超时"+Math.abs(mms)/(1000*60)+"分钟");
        }else{
            holder.effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
        }

        holder.grabFlagIV.setImageResource(R.mipmap.ic_launcher);
        holder.remarkFlagIV.setImageResource(R.mipmap.ic_launcher);
        fillItemListData(holder.itemContainerll, order.getItems());

        return convertView;
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            tv1.setMaxEms(9);
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
    static class ViewHolder{

        LinearLayout rootLayout;
        TextView orderIdTxt;
        TextView contantEffectTimeTxt;
        TextView effectTimeTxt;
        ImageView grabFlagIV;
        ImageView remarkFlagIV;
        LinearLayout itemContainerll;
        TextView produceBtn;
        TextView printBtn;
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        notifyDataSetChanged();
        selected = -1;
        //缓存订单列表
        order_list.clear();
        order_list.addAll(list);

        if(timerTask==null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG,"timerTask run ---"+Thread.currentThread().getId());
                    for(OrderBean order:order_list){
                        order.setProduceEffect(order.getProduceEffect()-DELTA_TIME);
                    }
                    handler.sendEmptyMessage(1);
                }
            };
            timer.schedule(timerTask,DELTA_TIME,DELTA_TIME);
        }else{

        }


    }

    private void refreshTimerData(List<OrderBean> order_list){
        this.list = order_list;
        notifyDataSetChanged();
    }
}
