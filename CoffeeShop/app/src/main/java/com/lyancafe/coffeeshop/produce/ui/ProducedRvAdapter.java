package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderCategory;
import com.lyancafe.coffeeshop.event.UpdateProducedDetailEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.OrderSortComparator;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ProducedRvAdapter extends RecyclerView.Adapter<ProducedRvAdapter.ViewHolder>{

    private static final String TAG  ="OrderGridViewAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    private List<OrderBean> searchList;
    public List<OrderBean> tempList;

    public ProducedRvAdapter(Context context) {
        this.context = context;
        searchList = new ArrayList<>();
        tempList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.produced_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知详情板块内容变更
                selected = position;
                notifyDataSetChanged();
                if(position>=0 && position<list.size()){
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(position)));
                }

                Log.d(TAG, "点击了 " + position);
            }
        });

        if(selected==position){
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order_selected);
        }else{
            holder.rootLayout.setBackgroundResource(R.drawable.bg_order);
        }
        final OrderBean order = list.get(position);

        holder.shopOrderIdText.setText(OrderHelper.getShopOrderSn(order));
        holder.deliverStatusText.setText(OrderHelper.getStatusName(order.getStatus(),order.getWxScan()));
        holder.deliverTeamText.setText(OrderHelper.getDeliverTeamName(order.getDeliveryTeam()));
        holder.deliverNameText.setText(order.getCourierName());
        holder.deliverPhoneText.setText(order.getCourierPhone());
        holder.orderIdText.setText(String.valueOf(order.getId()));
        holder.serviceEffectText.setText(OrderHelper.getTimeToService(order));


    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    //填充item数据
    private void fillItemListData(LinearLayout ll,List<ItemContentBean> items){
        ll.removeAllViews();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(context);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(6);
            tv1.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            if(!TextUtils.isEmpty(item.getRecipeFittings())){
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0,1,OrderHelper.dip2Px(12,context),OrderHelper.dip2Px(12,context));
                tv1.setCompoundDrawablePadding(OrderHelper.dip2Px(4,context));
                tv1.setCompoundDrawables(null, null,drawable,null);
            }
            TextView tv2 = new TextView(context);
            tv2.setText("x  " + item.getQuantity());
            tv2.setTextSize(context.getResources().getDimension(R.dimen.content_item_text_size));
            TextPaint tp = tv2.getPaint();
            tp.setFakeBoldText(true);
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2,context);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2,context);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2,context);
            ll.addView(rl,lp);
        }
        ll.invalidate();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.root_view) LinearLayout rootLayout;
        @BindView(R.id.ll_first_row) LinearLayout firstRowLayout;
        @BindView(R.id.tv_shop_order_id) TextView shopOrderIdText;
        @BindView(R.id.tv_service_effect) TextView serviceEffectText;
        @BindView(R.id.tv_deliver_status) TextView deliverStatusText;
        @BindView(R.id.tv_deliver_team) TextView deliverTeamText;
        @BindView(R.id.tv_deliver_name) TextView deliverNameText;
        @BindView(R.id.tv_deliver_phone) TextView deliverPhoneText;
        @BindView(R.id.tv_order_id) TextView orderIdText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        Collections.sort(this.list,new OrderSortComparator());
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
            EventBus.getDefault().post(new UpdateProducedDetailEvent(this.list.get(selected)));
        }else{
            EventBus.getDefault().post(new UpdateProducedDetailEvent(null));
        }

        tempList.clear();
        tempList.addAll(list);

    }

    public void setSearchData(List<OrderBean> list){
        this.list = list;
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
            EventBus.getDefault().post(new UpdateOrderDetailEvent(this.list.get(selected)));
        }else{
            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
        }
    }

    private List<OrderBean> filterOrders(List<OrderBean> list,int category){
        List<OrderBean> subList = new ArrayList<>();
        if(category== OrderCategory.MEITUN){
            for(OrderBean orderBean:list){
                if(orderBean.getDeliveryTeam()==8){
                    subList.add(orderBean);
                }
            }
        }else if(category==OrderCategory.OWN){
            for(OrderBean orderBean:list){
                if(orderBean.getDeliveryTeam()!=8){
                    subList.add(orderBean);
                }
            }
        }else{
            subList = list;
        }

        return subList;
    }

    //搜索
    public void searchOrder(final int shopOrderNo){
        Observable.fromIterable(tempList)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<OrderBean>() {
                    @Override
                    public boolean test(@NonNull OrderBean orderBean) throws Exception {
                        return orderBean.getShopOrderNo()==shopOrderNo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.d("xls","onSubscribe");
                        searchList.clear();
                    }

                    @Override
                    public void onNext(@NonNull OrderBean orderBean) {
                        LogUtil.d("xls","onNext");
                        searchList.add(orderBean);
                        setSearchData(searchList);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.d("xls","onError");
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.d("xls","onComplete");
                        if(searchList.size()==0){
                            ToastUtil.show(context,"没有搜到目标订单");
                        }
                    }
                });

    }



    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param orderId
     */
    public void removeOrderFromList(long orderId){
        for(int i=list.size()-1;i>=0;i--){
            if(list.get(i).getId()==orderId){
                list.remove(i);
                break;
            }
        }
        if(list.size()>0){
            selected=0;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateProducedDetailEvent(list.get(selected)));
        }else{
            selected = -1;
            notifyDataSetChanged();
            EventBus.getDefault().post(new UpdateProducedDetailEvent(null));
        }


    }


}
