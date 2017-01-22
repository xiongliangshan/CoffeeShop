package com.lyancafe.coffeeshop.adapter;

import android.app.Activity;
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
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/5.
 */
public class OrderListViewAdapter extends RecyclerView.Adapter<OrderListViewAdapter.ViewHolder>{


    private static final String TAG  ="OrderListViewAdapter";
    public List<SFGroupBean> groupList = new ArrayList<>();
    private Activity mContext;
    public static long selectedOrderId = 0;

    public OrderListViewAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public OrderListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sf_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OrderListViewAdapter.ViewHolder holder, int position) {
        final SFGroupBean sfGroupBean = groupList.get(position);
        holder.batchPromptText.setText(OrderHelper.createSFPromptStr(mContext, sfGroupBean));
        holder.horizontalListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalListView.setHasFixedSize(true);
        holder.horizontalListView.setItemAnimator(new DefaultItemAnimator());
        SFItemListAdapter adapter = new SFItemListAdapter(mContext,sfGroupBean.getItemGroup());
        holder.horizontalListView.setAdapter(adapter);
        if(OrdersFragment.tabIndex== TabList.TAB_TOPRODUCE){
            holder.batchHandlerBtn.setText(R.string.sf_batch_start);
        }else if(OrdersFragment.tabIndex== TabList.TAB_PRODUCING){
            holder.batchHandlerBtn.setText(R.string.sf_batch_produced);
        }else{
            holder.batchHandlerBtn.setText(R.string.sf_batch_print);
        }
        holder.batchHandlerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext.getString(R.string.sf_batch_start).equals(holder.batchHandlerBtn.getText())){
                //    new startBatchProduceQry(mContext,sfGroupBean.getId()).doRequest();
                    HttpHelper.getInstance().reqStartBatchProduce(sfGroupBean.getId(), new DialogCallback<XlsResponse>(mContext) {
                        @Override
                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                            handleStartBatchProduceResponse(xlsResponse,call,response);
                        }
                    });
                    //筛选出未打印的组内订单集合
                    List<OrderBean> unPrintList = OrderHelper.selectUnPrintList(mContext,sfGroupBean.getItemGroup());
                    //打印
                    PrintHelper.getInstance().printBatchCups(unPrintList);
                    PrintHelper.getInstance().printBatchBoxes(unPrintList);
                }else if(mContext.getString(R.string.sf_batch_produced).equals(holder.batchHandlerBtn.getText())){
                //    new DoFinishBatchProduceQry(mContext,sfGroupBean.getId()).doRequest();
                    HttpHelper.getInstance().reqFinishBatchProduce(sfGroupBean.getId(), new DialogCallback<XlsResponse>(mContext) {
                        @Override
                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                            handleFinishBatchProduceResponse(xlsResponse,call,response);
                        }
                    });
                }else{
                    //打印
                    PrintHelper.getInstance().printBatchCups(sfGroupBean.getItemGroup());
                    PrintHelper.getInstance().printBatchBoxes(sfGroupBean.getItemGroup());
                }

            }
        });
    }




    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public void setData(List<SFGroupBean> sfGroupList){
        this.groupList = sfGroupList;
        notifyDataSetChanged();
    }

    public void addData(List<SFGroupBean> sfGroupList){
        this.groupList.addAll(sfGroupList);
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

    /**
     * 点击开始生产，生产完成，扫码交付时从当前列表移除该订单
     * @param groupId 顺风单组Id
     */
    public void removeGroupFromList(int groupId,int produceStatus){
        for(int i=groupList.size()-1;i>=0;i--){
            if(groupId==groupList.get(i).getId()){
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
                notifyDataSetChanged();
                break;
            }
        }
    }


    /**
     * 处理开始批量生产
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleStartBatchProduceResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status == 0){
            ToastUtil.showToast(mContext, R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            removeGroupFromList(id, OrderStatus.PRODUCING);
        }else{
            ToastUtil.showToast(mContext, xlsResponse.message);
        }
    }


    /**
     * 处理批量生产完成
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleFinishBatchProduceResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status == 0){
            ToastUtil.showToast(mContext, R.string.do_success);
            int id  = xlsResponse.data.getIntValue("id");
            removeGroupFromList(id, OrderStatus.PRODUCED);
        }else{
            ToastUtil.showToast(mContext, xlsResponse.message);
        }
    }

    /*//顺风单组批量开始生产接口
    public class startBatchProduceQry implements Qry {
        private Context context;
        private int groupId;
        private boolean isShowDlg = true;

        public startBatchProduceQry(Context context, int groupId) {
            this.context = context;
            this.groupId = groupId;
        }

        public startBatchProduceQry(Context context, int groupId, boolean isShowDlg) {
            this.context = context;
            this.groupId = groupId;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            LoginBean loginBean = LoginHelper.getLoginBean(context);
            int shopId = loginBean.getShopId();
            String token = loginBean.getToken();
            String url = Urls.BASE_URL+shopId+"/order/"+groupId+"/beginproducebatch?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "startBatchProduceQry:resp = " + resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context, R.string.do_success);
                removeGroupFromList(groupId, OrderStatus.PRODUCING);
            }else{
                ToastUtil.showToast(context, resp.message);
            }
        }
    }*/

    /*//顺风单组批量完成操作接口
    public class DoFinishBatchProduceQry implements Qry{
        private Context context;
        private int groupId;
        private boolean isShowDlg = true;

        public DoFinishBatchProduceQry(Context context, int groupId) {
            this.context = context;
            this.groupId = groupId;
        }

        public DoFinishBatchProduceQry(Context context, int groupId, boolean isShowDlg) {
            this.context = context;
            this.groupId = groupId;
            this.isShowDlg = isShowDlg;
        }

        @Override
        public void doRequest() {
            LoginBean loginBean = LoginHelper.getLoginBean(context);
            int shopId = loginBean.getShopId();
            String token = loginBean.getToken();
            String url = Urls.BASE_URL+shopId+"/order/"+groupId+"/producebatch?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,isShowDlg);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "DoFinishBatchProduceQry:resp = " + resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status == 0){
                ToastUtil.showToast(context, R.string.do_success);
                removeGroupFromList(groupId, OrderStatus.PRODUCED);
            }else{
                ToastUtil.showToast(context, resp.message);
            }
        }
    }*/



}