package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ShopNoComparator;
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
public class FinishedRvAdapter extends RecyclerView.Adapter<FinishedRvAdapter.ViewHolder>{

    private static final String TAG  ="FinishedRvAdapter";
    private Context context;
    public List<OrderBean> list = new ArrayList<OrderBean>();
    public int selected = -1;
    private List<OrderBean> searchList;
    public List<OrderBean> tempList;
    private FinishedCallback callback;

    public FinishedRvAdapter(Context context) {
        this.context = context;
        searchList = new ArrayList<>();
        tempList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.finished_list_item, parent, false);
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
//                    EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(position)));
                    callback.updateDetail(list.get(position));
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

        if(DeliveryTeam.MEITUAN == order.getDeliveryTeam()){
            holder.shopOrderIdText.setText("美团");
            holder.deliverResultText.setText(String.valueOf(order.getThirdShopOrderNo()));
            holder.deliverNameText.setText("美团配送");
        } else if(DeliveryTeam.HAIKUI == order.getDeliveryTeam()){
            holder.shopOrderIdText.setText(OrderHelper.getShopOrderSn(order));
            holder.deliverResultText.setText(OrderHelper.getRealTimeToService(order));
            holder.deliverNameText.setText("海葵配送");
        } else{
            holder.shopOrderIdText.setText(OrderHelper.getShopOrderSn(order));
            holder.deliverResultText.setText(OrderHelper.getRealTimeToService(order));
            holder.deliverNameText.setText(order.getCourierName());
        }
        holder.orderTimeText.setText(OrderHelper.formatOrderDate(order.getOrderTime()));
        holder.expectedReachTimeText.setText(OrderHelper.getPeriodOfExpectedtime(order));
        holder.realReachTimeText.setText(OrderHelper.formatOrderDate(order.getHandoverTime()));
        holder.customEvaluationText.setText(order.getFeedbackType()==0?"无":order.getFeedbackType()==4?"好评":"差评");


    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view)
        CardView rootLayout;
        @BindView(R.id.tv_shop_order_id) TextView shopOrderIdText;
        @BindView(R.id.tv_deliver_result) TextView deliverResultText;
        @BindView(R.id.tv_order_time) TextView orderTimeText;
        @BindView(R.id.tv_expected_reach_time) TextView expectedReachTimeText;
        @BindView(R.id.tv_real_reach_time) TextView realReachTimeText;
        @BindView(R.id.tv_custom_evaluation) TextView customEvaluationText;
        @BindView(R.id.tv_deliver_name) TextView deliverNameText;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setData(List<OrderBean> list){
        this.list = list;
        Collections.sort(this.list,new ShopNoComparator());
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(this.list.get(selected)));
            callback.updateDetail(this.list.get(selected));
        }else{
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
            callback.updateDetail(null);
        }

        tempList.clear();
        tempList.addAll(list);

    }

    public void setSearchData(List<OrderBean> list){
        this.list = list;
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(this.list.get(selected)));
            callback.updateDetail(this.list.get(selected));
        }else{
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
            callback.updateDetail(null);
        }
    }

    public void addData(List<OrderBean> list){
        this.list.addAll(list);
        Collections.sort(this.list,new ShopNoComparator());
        notifyDataSetChanged();
        if(selected>=0 && selected<this.list.size()){
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(this.list.get(selected)));
            callback.updateDetail(this.list.get(selected));
        }else{
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
            callback.updateDetail(null);
        }
    }


    //搜索
    public void searchOrder(final int shopOrderNo){
        Observable.fromIterable(OrderUtils.with().queryFinishedOrders())
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
                            Logger.getLogger().log("已完成-没有搜到目标订单 "+shopOrderNo);
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
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(list.get(selected)));
            callback.updateDetail(list.get(selected));
        }else{
            selected = -1;
            notifyDataSetChanged();
//            EventBus.getDefault().post(new UpdateOrderDetailEvent(null));
            callback.updateDetail(null);
        }


    }


    public void setCallback(FinishedRvAdapter.FinishedCallback callback) {
        this.callback = callback;
    }

    interface FinishedCallback{
        void updateDetail(OrderBean order);
    }


}
