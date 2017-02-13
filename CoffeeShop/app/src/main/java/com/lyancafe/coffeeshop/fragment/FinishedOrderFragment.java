package com.lyancafe.coffeeshop.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.CommentActivity;
import com.lyancafe.coffeeshop.adapter.FinishedRvAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.ClickCommentEvent;
import com.lyancafe.coffeeshop.event.CommentCountEvent;
import com.lyancafe.coffeeshop.event.UpdateFinishedOrderDetailEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedOrderFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {

    private PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    private FinishedRvAdapter mAdapter;
    private long finishedLastOrderId = 0;
    private Context mContext;

    private TextView dateText;
    private TextView orderCountText;
    private TextView cupCountText;
    private TextView goodCommentText;
    private TextView badCommentText;
    /**
     * 订单详情页UI组件
     */
    private TextView orderIdTxt;
    private TextView receiveNameTxt;
    private TextView receivePhoneTxt;
    private TextView receiveAddressTxt;
    private TextView orderDistanceText;
    private LinearLayout itemsContainerLayout;
    private LinearLayout userRemarkLayout;
    private TextView userRemarkTxt;
    private LinearLayout csadRemarkLayout;
    private TextView csadRemarkTxt;
    private LinearLayout userCommentLayout;
    private TextView userCommentTagsText;
    private TextView userCommentContentText;
    /**
     * 订单详情
     */

    private IndoDetailListener indoDetailListener;

    public FinishedOrderFragment() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        indoDetailListener = new IndoDetailListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView = inflater.inflate(R.layout.fragment_finished_order, container, false);
        initViews(contentView);
        initDetailView(contentView);
        return contentView;
    }


    private void initViews(View contentView){
        dateText = (TextView) contentView.findViewById(R.id.tv_date);
        orderCountText = (TextView) contentView.findViewById(R.id.tv_order_count);
        cupCountText = (TextView) contentView.findViewById(R.id.tv_cup_count);
        goodCommentText = (TextView) contentView.findViewById(R.id.tv_good_comment);
        badCommentText = (TextView) contentView.findViewById(R.id.tv_bad_comment);
        pullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) contentView.findViewById(R.id.plmgv_order_list);

        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false) );
        pullLoadMoreRecyclerView.getRecyclerView().setHasFixedSize(true);
        pullLoadMoreRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pullLoadMoreRecyclerView.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(8, mContext), false));
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        pullLoadMoreRecyclerView.setRefreshing(false);
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setPushRefreshEnable(true);


        mAdapter = new FinishedRvAdapter(getActivity());
        pullLoadMoreRecyclerView.setAdapter(mAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateText.setText(sdf.format(new Date()));

        goodCommentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //好评列表
                Intent intent =  new Intent(mContext, CommentActivity.class);
                intent.putExtra("coment_type",OrderHelper.GOOD_COMMENT);
                mContext.startActivity(intent);
            }
        });

        badCommentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //差评列表
                Intent intent =  new Intent(mContext, CommentActivity.class);
                intent.putExtra("coment_type",OrderHelper.BAD_COMMENT);
                mContext.startActivity(intent);
            }
        });
    }

    private void initDetailView(View contentView){
        orderIdTxt = (TextView) contentView.findViewById(R.id.order_id);
        receiveNameTxt  = (TextView) contentView.findViewById(R.id.receiver_name);
        receivePhoneTxt = (TextView) contentView.findViewById(R.id.receiver_phone);
        receiveAddressTxt = (TextView) contentView.findViewById(R.id.receiver_address);
        orderDistanceText = (TextView) contentView.findViewById(R.id.tv_order_distance);
        itemsContainerLayout = (LinearLayout) contentView.findViewById(R.id.items_container_layout);
        userRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_remark);
        userRemarkLayout.setOnClickListener(indoDetailListener);
        userRemarkTxt = (TextView) contentView.findViewById(R.id.user_remark);
        csadRemarkLayout = (LinearLayout) contentView.findViewById(R.id.ll_csad_remark);
        csadRemarkLayout.setOnClickListener(indoDetailListener);
        csadRemarkTxt = (TextView) contentView.findViewById(R.id.csad_remark);
        userCommentLayout = (LinearLayout) contentView.findViewById(R.id.ll_user_comment);
        userCommentLayout.setOnClickListener(indoDetailListener);
        userCommentTagsText = (TextView) contentView.findViewById(R.id.user_comment_tags);
        userCommentContentText = (TextView) contentView.findViewById(R.id.user_comment_content);
    }

    private void updateDetailView(final OrderBean order){
        if(order==null){
            orderIdTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            orderDistanceText.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            userCommentTagsText.setText("");
            userCommentContentText.setText("");
            itemsContainerLayout.removeAllViews();
        }else{
            orderIdTxt.setText(order.getOrderSn());
            //服务时效
            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            orderDistanceText.setText(order.getOrderDistance()+"米");
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            userCommentTagsText.setText(OrderHelper.getCommentTagsStr(order.getFeedbackTags()));
            userCommentContentText.setText(order.getFeedback());

        }

    }
    //填充item数据
    private void fillItemListData(LinearLayout ll,final OrderBean order){
        if(order==null){
            return;
        }
        ll.removeAllViews();
        List<ItemContentBean> items = order.getItems();
        for(ItemContentBean item:items){
            TextView tv1 = new TextView(mContext);
            tv1.setId(R.id.item_name);
            tv1.setText(item.getProduct());
            tv1.setMaxEms(9);
            tv1.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv1.setTextColor(mContext.getResources().getColor(R.color.font_black));
            TextView tv2 = new TextView(mContext);
            tv2.setId(R.id.item_num);
            tv2.setText("x  " + item.getQuantity());
            tv2.getPaint().setFakeBoldText(true);
            tv2.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));

            RelativeLayout rl = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams lp1=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2,mContext);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2,mContext);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            String dingzhi = OrderHelper.getLabelStr(item.getRecipeFittingsList());
            if(!TextUtils.isEmpty(dingzhi)){
                TextView tv5 = new TextView(mContext);
                tv5.setId(R.id.item_flag);
                tv5.setText(dingzhi);
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0, 1, OrderHelper.dip2Px(14, mContext), OrderHelper.dip2Px(14, mContext));
                tv5.setCompoundDrawablePadding(OrderHelper.dip2Px(4, mContext));
                tv5.setCompoundDrawables(drawable, null, null, null);
                tv5.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
                tv5.setTextColor(mContext.getResources().getColor(R.color.font_black));
                RelativeLayout.LayoutParams lp5=new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp5.addRule(RelativeLayout.BELOW,tv1.getId());
                lp5.addRule(RelativeLayout.ALIGN_LEFT,tv1.getId());
                tv5.setLayoutParams(lp5);
                rl.addView(tv5);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2,mContext);
            ll.addView(rl,lp);
        }
        if(!TextUtils.isEmpty(order.getWishes())){
            TextView tv3 = new TextView(mContext);
            tv3.setText("礼品卡");
            tv3.setMaxEms(9);
            tv3.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv3.setTextColor(mContext.getResources().getColor(R.color.font_black));
            TextView tv4 = new TextView(mContext);
            tv4.setText(order.getWishes());
            tv4.getPaint().setFakeBoldText(true);
            tv4.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            RelativeLayout r2 = new RelativeLayout(mContext);
            r2.setBackgroundColor(Color.YELLOW);
            RelativeLayout.LayoutParams lp3=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp3.leftMargin = OrderHelper.dip2Px(2,mContext);
            tv3.setLayoutParams(lp3);
            r2.addView(tv3);

            RelativeLayout.LayoutParams lp4=new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp4.rightMargin = OrderHelper.dip2Px(2,mContext);
            tv4.setLayoutParams(lp4);
            r2.addView(tv4);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4,mContext);
            ll.addView(r2, lp);
        }

        TextView tv6 = new TextView(mContext);
        tv6.setText(mContext.getResources().getString(R.string.total_quantity, OrderHelper.getTotalQutity(order)));
        tv6.setGravity(Gravity.CENTER);
        tv6.getPaint().setFakeBoldText(true);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp6.topMargin = OrderHelper.dip2Px(30,mContext);
        tv6.setLayoutParams(lp6);
        ll.addView(tv6, lp6);

        ll.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpHelper.getInstance().reqFinishedTotalAmountData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleFinishedTotalAmountResponse(xlsResponse,call,response);
            }

        });
        HttpHelper.getInstance().reqFinishedListData(0, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleFinishedResponse(xlsResponse, call, response);
            }
        });
    }

    @Subscribe
    public void onUpdateFinishedOrderDetailEvent(UpdateFinishedOrderDetailEvent event){
        updateDetailView(event.orderBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentCountEvent(CommentCountEvent event){
        HttpHelper.getInstance().reqCommentCount(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleCommentCountResponse(xlsResponse,call,response);
            }
        });
    }

    @Subscribe
    public void onClickCommentEvent(ClickCommentEvent event){
        updateDetailView(event.orderBean);
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {

    }



    @Override
    public void onLoadMore() {
        HttpHelper.getInstance().reqFinishedListData(finishedLastOrderId, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                handleFinishedResponse(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });
    }


    /**
     * 处理评论数量接口返回的数据
     */
    private void handleCommentCountResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            int positive = xlsResponse.data.getIntValue("positive");
            int negative = xlsResponse.data.getIntValue("negative");
            if(goodCommentText!=null && badCommentText!=null){
                goodCommentText.setText("好评"+positive);
                badCommentText.setText("差评"+negative);
            }

        }
    }

    //处理服务器返回数据---已完成
    private void handleFinishedResponse(XlsResponse xlsResponse,Call call,Response response){
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
        if("yes".equalsIgnoreCase(isLoadMore)){
            mAdapter.addData(orderBeans);
            Log.d("xls","addData orderBeans.size = "+orderBeans.size());
        }else{
            mAdapter.setData(orderBeans);
            Log.d("xls","orderBeans.size = "+orderBeans.size());
        }

        if(mAdapter.list.size()>0){
            finishedLastOrderId = mAdapter.list.get(mAdapter.list.size()-1).getId();
        }else {
            finishedLastOrderId = 0;
        }
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        HttpHelper.getInstance().reqFinishedTotalAmountData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleFinishedTotalAmountResponse(xlsResponse,call,response);
            }

        });
        HttpHelper.getInstance().reqFinishedListData(0, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleFinishedResponse(xlsResponse, call, response);
            }
        });
    }


    //处理服务器返回的已完成订单总单量和杯量
    private void handleFinishedTotalAmountResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            int ordersAmount = xlsResponse.data.getIntValue("totalOrdersAmount");
            int cupsAmount = xlsResponse.data.getIntValue("totalCupsAmount");
            orderCountText.setText(String.valueOf(ordersAmount));
            cupCountText.setText(String.valueOf(cupsAmount));
        }
    }


    class  IndoDetailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_user_remark:
                    //用户备注
                    new InfoDetailDialog(getActivity()).show(userRemarkTxt.getText().toString());
                    break;
                case R.id.ll_csad_remark:
                    //客服备注
                    new InfoDetailDialog(getActivity()).show(csadRemarkTxt.getText().toString());
                    break;
                case R.id.ll_user_comment:
                    //用户评价
                    new InfoDetailDialog(getActivity()).show(userCommentTagsText.getText().toString()+"\n"+userCommentContentText.getText().toString());
                    break;
            }
        }
    }
}
