package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.lyancafe.coffeeshop.activity.PrintOrderActivity;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.ConfirmDialog;
import com.lyancafe.coffeeshop.widget.SimpleConfirmDialog;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import org.greenrobot.eventbus.EventBus;

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
    public static  ArrayList<OrderBean> testList =  new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheToProduceList =  new ArrayList<OrderBean>();
    public ArrayList<OrderBean> cacheProducingList = new ArrayList<OrderBean>();
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
        final ViewHolder holder ;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item,null);
            holder  = new ViewHolder();
            holder.rootLayout = (RelativeLayout) convertView.findViewById(R.id.root_view);
            holder.secondRootLayout = (LinearLayout) convertView.findViewById(R.id.second_root_view);
            holder.giftIV = (ImageView) convertView.findViewById(R.id.iv_gift);
            holder.labelFlagImg = (ImageView) convertView.findViewById(R.id.iv_label_flag);
            holder.logoScanIV = (ImageView) convertView.findViewById(R.id.logo_scan);
            holder.orderIdTxt = (TextView) convertView.findViewById(R.id.item_order_id);
            holder.contantEffectTimeTxt = (TextView) convertView.findViewById(R.id.item_contant_produce_effect);
            holder.effectTimeTxt = (TextView) convertView.findViewById(R.id.item_produce_effect);
            holder.issueFlagIV = (ImageView) convertView.findViewById(R.id.item_issue_flag);
            holder.grabFlagIV = (ImageView) convertView.findViewById(R.id.item_grab_flag);
            holder.remarkFlagIV = (ImageView) convertView.findViewById(R.id.item_remark_flag);
            holder.itemContainerll = (LinearLayout) convertView.findViewById(R.id.item_container);
            holder.twobtnContainerLayout = (LinearLayout) convertView.findViewById(R.id.ll_twobtn_container);
            holder.onebtnContainerlayout = (LinearLayout) convertView.findViewById(R.id.ll_onebtn_container);
            holder.produceBtn = (TextView) convertView.findViewById(R.id.item_produce);
            holder.printBtn = (TextView) convertView.findViewById(R.id.item_print);
            holder.produceAndPrintBtn = (TextView) convertView.findViewById(R.id.item_produce_and_print);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.mipmap.touch_border);
        }else{
            holder.rootLayout.setBackground(null);
        }

        final OrderBean order = list.get(position);
        if(OrderHelper.isBatchOrder(order.getId())){
            holder.secondRootLayout.setBackgroundResource(R.drawable.bg_batch_order);
            holder.itemContainerll.setBackgroundResource(R.mipmap.bg_batch_dot);
        }else{
            holder.secondRootLayout.setBackgroundResource(R.drawable.bg_order);
            holder.itemContainerll.setBackgroundResource(R.mipmap.bg_normal_dot);
        }
        holder.orderIdTxt.setText(OrderHelper.getShopOrderSn(order.getInstant(),order.getShopOrderNo()));
        if(order.isWxScan()){
            holder.logoScanIV.setVisibility(View.VISIBLE);
        }else{
            holder.logoScanIV.setVisibility(View.GONE);
        }
        if(order.getGift()==2){
            //礼盒订单
            holder.giftIV.setVisibility(View.VISIBLE);
        }else{
            holder.giftIV.setVisibility(View.INVISIBLE);
        }

        if(order.isRecipeFittings()){
            holder.labelFlagImg.setVisibility(View.VISIBLE);
        }else{
            holder.labelFlagImg.setVisibility(View.INVISIBLE);
        }
        if(OrdersFragment.subTabIndex==0){
            if(order.getInstant()==0){
                holder.produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn_blue);
            }else{
                holder.produceAndPrintBtn.setBackgroundResource(R.drawable.bg_produce_btn);
            }
        }else{
            OrderHelper.showEffect(order, holder.produceBtn, holder.effectTimeTxt);
        }


        if(OrderHelper.isPrinted(context, order.getOrderSn())){
            holder.printBtn.setText(R.string.print_again);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_red));
        }else{
            holder.printBtn.setText(R.string.print);
            holder.printBtn.setTextColor(context.getResources().getColor(R.color.text_black));
        }
        if(order.issueOrder()){
            holder.issueFlagIV.setVisibility(View.VISIBLE);
        }else{
            holder.issueFlagIV.setVisibility(View.INVISIBLE);
        }
        if(order.getStatus()== OrderStatus.UNASSIGNED){
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
        if(OrdersFragment.subTabIndex == 0){
            holder.twobtnContainerLayout.setVisibility(View.GONE);
            holder.onebtnContainerlayout.setVisibility(View.VISIBLE);
            holder.produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击开始生产（打印）按钮
                    startProduceAndPrint(context,order);
                }
            });
        }else{
            holder.twobtnContainerLayout.setVisibility(View.VISIBLE);
            holder.onebtnContainerlayout.setVisibility(View.GONE);
            if(OrdersFragment.subTabIndex!=1){
                holder.produceBtn.setEnabled(false);
            }else{
                holder.produceBtn.setEnabled(true);
            }
            holder.produceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //生产完成
                    if (order.getInstant() == 0) {
                        //预约单
                        if (!OrderHelper.isCanHandle(order)) {
                            //生产时间还没到
                            SimpleConfirmDialog scd = new SimpleConfirmDialog(context, R.style.MyDialog);
                            scd.setContent(R.string.can_not_operate);
                            scd.show();
                        } else {
                            ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                                @Override
                                public void onClickYes() {
                                    Log.d(TAG, "postion = " + position + ",orderId = " + order.getOrderSn());
                                    new DoFinishProduceQry(order.getId()).doRequest();
                                }
                            });
                            grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
                            grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                            grabConfirmDialog.show();
                        }

                    } else {
                        //及时单
                        ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                            @Override
                            public void onClickYes() {
                                Log.d(TAG, "postion = " + position + ",orderId = " + order.getOrderSn());
                                new DoFinishProduceQry(order.getId()).doRequest();
                            }
                        });
                        grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 生产完成？");
                        grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                        grabConfirmDialog.show();
                    }

                }
            });
            holder.printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderFragment.printOrder(context,order);
                }
            });
        }


        return convertView;
    }

    //开始生产并打印
    private void startProduceAndPrint(Context context,final OrderBean order){
        if (order.getInstant() == 0) {
            //预约单
            if (!OrderHelper.isCanHandle(order)) {
                //预约单，生产时间还没到
                SimpleConfirmDialog scd = new SimpleConfirmDialog(context, R.style.MyDialog);
                scd.setContent(R.string.can_not_operate);
                scd.show();
            } else {
                ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                    @Override
                    public void onClickYes() {
                        Log.d(TAG, "orderId = " + order.getOrderSn());
                        //请求服务器改变该订单状态，由 待生产--生产中
                        new startProduceQry(order.getId()).doRequest();
                        //打印全部
                        PrintHelper.getInstance().printOrderInfo(order);
                        PrintHelper.getInstance().printOrderItems(order);
                    }
                });
                grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
                grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
                grabConfirmDialog.show();
            }

        } else {
            //及时单
            ConfirmDialog grabConfirmDialog = new ConfirmDialog(context, R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
                @Override
                public void onClickYes() {
                    Log.d(TAG, "orderId = " + order.getOrderSn());
                    //请求服务器改变该订单状态，由 待生产--生产中
                    new startProduceQry(order.getId()).doRequest();
                    //打印全部
                    PrintHelper.getInstance().printOrderInfo(order);
                    PrintHelper.getInstance().printOrderItems(order);
                }
            });
            grabConfirmDialog.setContent("订单 " + order.getOrderSn() + " 开始生产？");
            grabConfirmDialog.setBtnTxt(R.string.click_error, R.string.confirm);
            grabConfirmDialog.show();
        }
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct() + "(" + item.getUnit() + ")");
            tv1.setMaxEms(6);
            tv1.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            TextView tv2 = new TextView(context);
            tv2.setText("X " + item.getQuantity());
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
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

        RelativeLayout rootLayout;
        LinearLayout secondRootLayout;
        ImageView giftIV;
        ImageView labelFlagImg;
        ImageView logoScanIV;
        TextView orderIdTxt;
        TextView contantEffectTimeTxt;
        TextView effectTimeTxt;
        ImageView issueFlagIV;
        ImageView grabFlagIV;
        ImageView remarkFlagIV;
        LinearLayout itemContainerll;
        LinearLayout twobtnContainerLayout;
        LinearLayout onebtnContainerlayout;
        TextView produceAndPrintBtn;
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
            testList = (ArrayList<OrderBean>) cacheToProduceList.clone();
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
            cacheProducingList.clear();
            cacheProducingList.addAll(list);

        } else if(OrdersFragment.subTabIndex==2){
            cacheProducedList.clear();
            cacheProducedList.addAll(list);
        }




    }

    private void refreshTimerData(List<OrderBean> order_list){
        this.list = order_list;
        notifyDataSetChanged();
    }


    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param orderId
     * @param list
     */
    public void removeOrderFromList(long orderId,List<OrderBean> list){
        for(int i = 0;i<list.size();i++){
            if(list.get(i).getId()==orderId){
                list.remove(i);
                this.list = list;
                notifyDataSetChanged();
                break;
            }
        }
    }

    //生产完成操作接口
    public class DoFinishProduceQry implements Qry{
        private long orderId;
        private boolean isShowDlg = true;

        public DoFinishProduceQry(long orderId) {
            this.orderId = orderId;
        }

        public DoFinishProduceQry(long orderId,boolean isShowDlg) {
            this.orderId = orderId;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            int shopId = LoginHelper.getShopId(context);
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/produce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"DoFinishProduceQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context, R.string.do_success);
                removeOrderFromList(orderId, cacheProducingList);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.FINISHPRODUCE));
            //    int leftBatchOrderNum = OrderHelper.removeOrderFromBatchList(orderId);
            //    orderFragment.updateBatchPromptTextView(leftBatchOrderNum);
            }else{
                ToastUtil.showToast(context,resp.message);
            }
        }
    }

    //开始生产接口

    public class startProduceQry implements Qry{
        private long orderId;
        private boolean isShowDlg = true;

        public startProduceQry(long orderId) {
            this.orderId = orderId;
        }

        public startProduceQry(long orderId,boolean isShowDlg) {
            this.orderId = orderId;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            int shopId = LoginHelper.getShopId(context);
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/beginproduce?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"startProduceQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context,R.string.do_success);
                removeOrderFromList(orderId, cacheToProduceList);
                EventBus.getDefault().post(new ChangeTabCountByActionEvent(OrderAction.STARTPRODUCE));
            }else{
                ToastUtil.showToast(context,resp.message);
            }
        }
    }


}
