package com.lyancafe.coffeeshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.AssignOrderActivity;
import com.lyancafe.coffeeshop.adapter.DeliverFragmentPagerAdapter;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.event.UpdateDeliverOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class DeliverFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] titles = new String[]{"待取货","配送中"};
    public static int tabIndex = 0;

    private Context mContext;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DeliverFragmentPagerAdapter mPagerAdapter;

    /**
     * 订单详情页UI组件
     */
    private TextView orderIdTxt;
    private TextView wholeOrderText;
    private TextView orderTimeTxt;
    private TextView reachTimeTxt;
    private TextView receiveNameTxt;
    private TextView receivePhoneTxt;
    private TextView receiveAddressTxt;
    private TextView deliverNameTxt;
    private TextView deliverPhoneTxt;
    private LinearLayout itemsContainerLayout;
    private LinearLayout userRemarkLayout;
    private TextView userRemarkTxt;
    private LinearLayout csadRemarkLayout;
    private TextView csadRemarkTxt;
    private LinearLayout userCommentLayout;
    private TextView userCommentTagsText;
    private TextView userCommentContentText;
    private LinearLayout twoBtnLayout;
    private LinearLayout oneBtnLayout;
    private Button produceAndPrintBtn;
    private Button finishProduceBtn;
    private Button printOrderBtn;
    private Button moreBtn;
    /**
     * 订单详情
     */

    private IndoDetailListener indoDetailListener;



    public DeliverFragment() {

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliverFragment newInstance(String param1, String param2) {
        DeliverFragment fragment = new DeliverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        Log.d("xls","DeliverFragment-onCreate");
        indoDetailListener = new IndoDetailListener();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_deliver, container, false);
        initViews(contentView);
        initDetailView(contentView);
        return contentView;
    }

    private void initViews(View contentView) {
        tabLayout  = (TabLayout) contentView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_container);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ToFetchFragment());
        fragments.add(new DeliveringFragment());
        mPagerAdapter = new DeliverFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

    }

    private void initDetailView(View contentView){
        orderIdTxt = (TextView) contentView.findViewById(R.id.order_id);
        wholeOrderText = (TextView) contentView.findViewById(R.id.tv_whole_order_sn);
        orderTimeTxt = (TextView) contentView.findViewById(R.id.order_time);
        reachTimeTxt = (TextView) contentView.findViewById(R.id.reach_time);
        receiveNameTxt  = (TextView) contentView.findViewById(R.id.receiver_name);
        receivePhoneTxt = (TextView) contentView.findViewById(R.id.receiver_phone);
        receiveAddressTxt = (TextView) contentView.findViewById(R.id.receiver_address);
        deliverNameTxt = (TextView) contentView.findViewById(R.id.deliver_name);
        deliverPhoneTxt = (TextView) contentView.findViewById(R.id.deliver_phone);
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
        twoBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_twobtn);
        oneBtnLayout = (LinearLayout) contentView.findViewById(R.id.ll_onebtn);
        produceAndPrintBtn = (Button) contentView.findViewById(R.id.btn_produce_print);
        finishProduceBtn = (Button) contentView.findViewById(R.id.btn_finish_produce);
        printOrderBtn = (Button) contentView.findViewById(R.id.btn_print_order);
        moreBtn  = (Button) contentView.findViewById(R.id.btn_more);
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.d("xls","DeliverFragment-onResume, postion="+tabLayout.getSelectedTabPosition());
    }

    @Subscribe
    public void onUpdateDeliverFragmentTabOrderCount(UpdateDeliverFragmentTabOrderCount event){
        TabLayout.Tab tab = tabLayout.getTabAt(event.tabIndex);
        if(event.count>0){
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex)+"("+event.count+")");
        }else{
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex));
        }

    }

    @Subscribe
    public void onUpdateDeliverOrderDetailEvent(UpdateDeliverOrderDetailEvent event){
        updateDetailView(event.orderBean);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","DeliverFragment-onPause");
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabIndex = tab.getPosition();
        viewPager.setCurrentItem(tabIndex);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    protected void onVisible() {
//        Log.d("xls","DeliverFragment is onVisible , resume :"+isResumed()+"| hide:"+isHidden());
        if(!isResumed()){
            return;
        }
        for(int i=0;i<mPagerAdapter.getCount();i++){
            if(i==tabIndex){
                mPagerAdapter.getItem(i).setUserVisibleHint(true);
            }else{
                mPagerAdapter.getItem(i).setUserVisibleHint(false);
            }
        }

    }

    @Override
    protected void onInVisible() {
//        Log.d("xls","DeliverFragment is onInVisible");
    }


    private void updateDetailView(final OrderBean order){
        if(order==null){
            orderIdTxt.setText("");
            wholeOrderText.setText("");
            orderTimeTxt.setText("");
            reachTimeTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            deliverNameTxt.setText("");
            deliverPhoneTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            userCommentTagsText.setText("");
            userCommentContentText.setText("");
            itemsContainerLayout.removeAllViews();
            produceAndPrintBtn.setEnabled(false);
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            moreBtn.setEnabled(false);
        }else{
            produceAndPrintBtn.setEnabled(true);
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);
            moreBtn.setEnabled(true);

            orderIdTxt.setText(OrderHelper.getShopOrderSn(order));
            wholeOrderText.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));

            //服务时效
            reachTimeTxt.setText(OrderHelper.getTimeToService(order));
            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            deliverNameTxt.setText(order.getCourierName());
            deliverPhoneTxt.setText(order.getCourierPhone());
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());
            userCommentTagsText.setText(OrderHelper.getCommentTagsStr(order.getFeedbackTags()));
            userCommentContentText.setText(order.getFeedback());

            if(order.getProduceStatus() == OrderStatus.UNPRODUCED){
                twoBtnLayout.setVisibility(View.GONE);
                oneBtnLayout.setVisibility(View.VISIBLE);
                produceAndPrintBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击开始生产（打印）按钮
                        EventBus.getDefault().post(new StartProduceEvent(order));
                    }
                });
            }else if(order.getProduceStatus() == OrderStatus.PRODUCING){
                twoBtnLayout.setVisibility(View.VISIBLE);
                oneBtnLayout.setVisibility(View.GONE);
                finishProduceBtn.setVisibility(View.VISIBLE);
                finishProduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //生产完成
                        EventBus.getDefault().post(new FinishProduceEvent(order));
                    }
                });
                if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                }else{
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new PrintOrderEvent(order));
                    }
                });
            }else{
                twoBtnLayout.setVisibility(View.VISIBLE);
                oneBtnLayout.setVisibility(View.GONE);
                finishProduceBtn.setVisibility(View.GONE);
                if(OrderHelper.isPrinted(mContext, order.getOrderSn())){
                    printOrderBtn.setText(R.string.print_again);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                }else{
                    printOrderBtn.setText(R.string.print);
                    printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                }
                printOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new PrintOrderEvent(order));
                    }
                });
            }

            if(order.getDeliveryTeam()== DeliveryTeam.MEITUAN){
                moreBtn.setEnabled(false);
            }else{
                moreBtn.setEnabled(true);
                moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popup = new PopupMenu(mContext, v);
                        popup.inflate(R.menu.menu_order_detail_more);
                        if (order.getStatus() != OrderStatus.ASSIGNED) {
                            popup.getMenu().findItem(R.id.menu_undo_order).setVisible(false);
                        }
                        if (order.getStatus() != OrderStatus.UNASSIGNED) {
                            popup.getMenu().findItem(R.id.menu_assign_order).setVisible(false);
                        }
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_undo_order:
                                        HttpHelper.getInstance().reqRecallOrder(order.getId(), new DialogCallback<XlsResponse>(getActivity()) {

                                            @Override
                                            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                                                EventBus.getDefault().post(new RecallOrderEvent(xlsResponse,call,response));
                                            }
                                        });
                                        break;
                                    case R.id.menu_assign_order:
                                        Intent intent = new Intent(mContext, AssignOrderActivity.class);
                                        intent.putExtra("orderId", order.getId());
                                        mContext.startActivity(intent);
                                        break;
                                }
                                popup.dismiss();
                                return false;
                            }
                        });

                        popup.show();
                    }
                });
            }

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


    class  IndoDetailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_user_remark:
                    //用户备注
                    InfoDetailDialog.getInstance(mContext).show(userRemarkTxt.getText().toString());
                    break;
                case R.id.ll_csad_remark:
                    //客服备注
                    InfoDetailDialog.getInstance(mContext).show(csadRemarkTxt.getText().toString());
                    break;
                case R.id.ll_user_comment:
                    //用户评价
                    InfoDetailDialog.getInstance(mContext).show(userCommentTagsText.getText().toString()+"\n"+userCommentContentText.getText().toString());
                    break;
            }
        }
    }




}
