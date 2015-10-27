package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.PrinterActivity;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderGridViewAdapter extends BaseAdapter{

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    private OrdersFragment orderFragment;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    public Timer timer;
    private TimerTask timerTask;
    private final static long DELTA_TIME = 30*1000;//单位ms
    public ArrayList<OrderBean> cacheToProduceList =  new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheProducedList =  new ArrayList<OrderBean>();
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(OrdersFragment.subTabIndex==0){
                        refreshTimerData(cacheToProduceList);
                    }
                    break;
            }

        }
    };

    public OrderGridViewAdapter(Context context,Fragment fragment) {
        this.context = context;
        timer =  new Timer(true);
        this.orderFragment = (OrdersFragment)fragment;
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
            holder.issueFlagIV = (ImageView) convertView.findViewById(R.id.item_issue_flag);
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
            holder.rootLayout.setBackgroundResource(R.mipmap.touch_border);
        }else{
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order);
        }
        final OrderBean order = list.get(position);
        holder.orderIdTxt.setText(order.getOrderSn());
        final long mms = order.getProduceEffect();
        Log.d(TAG, "mms = " + mms);

        if(OrdersFragment.subTabIndex==0){
            holder.produceBtn.setEnabled(true);

            if(mms<=0){
                holder.effectTimeTxt.setTextColor(Color.parseColor("#e2435a"));
                holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
                holder.effectTimeTxt.setText("+"+OrderHelper.getDateToMinutes(Math.abs(mms)));
            }else{
                if(order.getInstant()==0){
                    holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
                }else{
                    holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
                }
                holder.effectTimeTxt.setTextColor(Color.parseColor("#000000"));
                holder.effectTimeTxt.setText(OrderHelper.getDateToMinutes(mms));
            }


        }else{
            holder.produceBtn.setEnabled(false);
            holder.produceBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            holder.effectTimeTxt.setTextColor(Color.parseColor("#000000"));
            holder.effectTimeTxt.setText("-----");
        }

        if(order.issueOrder()){
            holder.issueFlagIV.setVisibility(View.VISIBLE);
        }else{
            holder.issueFlagIV.setVisibility(View.INVISIBLE);
        }
        if(order.getStatus()==OrderHelper.UNASSIGNED_STATUS){
            holder.grabFlagIV.setVisibility(View.INVISIBLE);
        }else{
            holder.grabFlagIV.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(order.getNotes()) && TextUtils.isEmpty(order.getCsrNotes())){
            holder.remarkFlagIV.setVisibility(View.INVISIBLE);
        }else {
            holder.remarkFlagIV.setVisibility(View.VISIBLE);
        }
        fillItemListData(holder.itemContainerll, order.getItems());
        holder.produceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生产完成
                ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener(){
                    @Override
                    public void onClickYes() {
                        Log.d(TAG,"postion = "+position+",orderId = "+order.getOrderSn());
                        new DoFinishProduceQry(order.getId()).doRequest();
                    }
                });
                grabConfirmDialog.setContent("订单 "+order.getOrderSn()+" 生产完成？");
                grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                grabConfirmDialog.show();

            }
        });
        holder.printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrinterActivity.class);
                intent.putExtra("order",order);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            tv1.setMaxEms(6);
            tv1.setTextSize(OrderHelper.sp2px(context, 16));
            TextView tv2 = new TextView(context);
            tv2.setText("X " + item.getQuantity());
            tv2.setTextSize(OrderHelper.sp2px(context, 16));
            TextPaint tp = tv2.getPaint();
            tp.setFakeBoldText(true);
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(5,context);
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
            lp.topMargin = OrderHelper.dip2Px(4,context);
            ll.addView(rl,lp);
        }
        ll.invalidate();
    }



    static class ViewHolder{

        LinearLayout rootLayout;
        TextView orderIdTxt;
        TextView contantEffectTimeTxt;
        TextView effectTimeTxt;
        ImageView issueFlagIV;
        ImageView grabFlagIV;
        ImageView remarkFlagIV;
        LinearLayout itemContainerll;
        TextView produceBtn;
        TextView printBtn;
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        notifyDataSetChanged();
        selected = 0;
        if(OrdersFragment.subTabIndex==0){
            //缓存订单列表
            cacheToProduceList.clear();
            cacheToProduceList.addAll(list);

            if(timerTask==null){
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG,"timerTask run ---"+Thread.currentThread().getId());
                        for(OrderBean order:cacheToProduceList){
                            order.setProduceEffect(order.getProduceEffect()-DELTA_TIME);
                        }
                        handler.sendEmptyMessage(1);
                    }
                };
                timer.schedule(timerTask,DELTA_TIME,DELTA_TIME);
            }

        }else if(OrdersFragment.subTabIndex==1){
            //缓存订单列表
            cacheProducedList.clear();
            cacheProducedList.addAll(list);

        }




    }

    private void refreshTimerData(List<OrderBean> order_list){
        this.list = order_list;
        notifyDataSetChanged();
    }

    //生产完成后从当前列表中移除此单
    private void removeOrderFromList(long orderId){
        for(int i = 0;i<cacheToProduceList.size();i++){
            if(cacheToProduceList.get(i).getId()==orderId){
                cacheToProduceList.remove(i);
                this.list = cacheToProduceList;
                notifyDataSetChanged();
                break;
            }
        }
    }
    //交付完成后从当前列表中移除此单
    public void removeOrderFromProducedList(long orderId){
        for(int i = 0;i<cacheProducedList.size();i++){
            if(cacheProducedList.get(i).getId()==orderId){
                cacheProducedList.remove(i);
                this.list = cacheProducedList;
                notifyDataSetChanged();
                break;
            }
        }
    }
    //生产完成操作接口
    public class DoFinishProduceQry implements Qry{
        private long orderId;

        public DoFinishProduceQry(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void doRequest() {
            int shopId = LoginHelper.getShopId(context);
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/produce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"DoFinishProduceQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context,R.string.do_success);
                removeOrderFromList(orderId);
                orderFragment.updateOrdersNumAfterAction(OrdersFragment.ACTION_PRODUCE);
            }else{
                ToastUtil.showToast(context,resp.message);
            }
        }
    }
}
