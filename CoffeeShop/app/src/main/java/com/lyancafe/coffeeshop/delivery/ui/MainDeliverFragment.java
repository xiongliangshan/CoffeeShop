package com.lyancafe.coffeeshop.delivery.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderCategory;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.delivery.presenter.MainDeliverPresenter;
import com.lyancafe.coffeeshop.delivery.presenter.MainDeliverPresenterImpl;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.event.UpdateDeliverOrderDetailEvent;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.produce.ui.AssignOrderActivity;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainDeliverFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, AdapterView.OnItemSelectedListener {

    public static int tabIndex = 0;
    public static int category = OrderCategory.ALL;

    private Context mContext;
    private CourierFragment courierFragment;
    private ToFetchFragment toFetchFragment;
    private DeliveringFragment deliveringFragment;
    private DeliverFragmentPagerAdapter mPagerAdapter;
    private Unbinder unbinder;
    private OrderBean mOrder;

    private MainDeliverPresenter mMainDeliverPresenter;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.vp_container)
    ViewPager viewPager;
    @BindView(R.id.layout_detail)
    View detailLayout;
    /**
     * 订单详情页UI组件
     */
    @BindView(R.id.order_id)
    TextView orderIdTxt;
    @BindView(R.id.tv_whole_order_sn)
    TextView wholeOrderText;
    @BindView(R.id.order_time)
    TextView orderTimeTxt;
    @BindView(R.id.reach_time)
    TextView reachTimeTxt;
    @BindView(R.id.receiver_name)
    TextView receiveNameTxt;
    @BindView(R.id.receiver_phone)
    TextView receivePhoneTxt;
    @BindView(R.id.receiver_address)
    TextView receiveAddressTxt;
    @BindView(R.id.deliver_name)
    TextView deliverNameTxt;
    @BindView(R.id.deliver_phone)
    TextView deliverPhoneTxt;
    @BindView(R.id.items_container_layout)
    LinearLayout itemsContainerLayout;
    @BindView(R.id.ll_user_remark)
    LinearLayout userRemarkLayout;
    @BindView(R.id.user_remark)
    TextView userRemarkTxt;
    @BindView(R.id.ll_csad_remark)
    LinearLayout csadRemarkLayout;
    @BindView(R.id.csad_remark)
    TextView csadRemarkTxt;
    @BindView(R.id.contant_issue_feedback)
    TextView reportIssueBtn;
    @BindView(R.id.btn_assign)
    TextView assignBtn;
    @BindView(R.id.btn_print_order)
    Button printOrderBtn;
    /**
     * 订单详情
     */

    @BindView(R.id.spinner_category)
    AppCompatSpinner spinnerCategory;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mMainDeliverPresenter = new MainDeliverPresenterImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_deliver, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        List<Fragment> fragments = new ArrayList<>();
        courierFragment = new CourierFragment();
        toFetchFragment = new ToFetchFragment();
        deliveringFragment = new DeliveringFragment();
        fragments.add(courierFragment);
        fragments.add(toFetchFragment);
        fragments.add(deliveringFragment);
        mPagerAdapter = new DeliverFragmentPagerAdapter(getChildFragmentManager(), getActivity(), fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        spinnerCategory.setOnItemSelectedListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (courierFragment != null) {
            courierFragment.onVisible();
        }
        if(detailLayout==null){
            return;
        }
        if(tabIndex==0){
            detailLayout.setVisibility(View.GONE);
        }else{
            detailLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        switch (position) {
            case 0:
                category = OrderCategory.ALL;
                break;
            case 1:
                category = OrderCategory.MEITUN;
                break;
            case 2:
                category = OrderCategory.OWN;
                break;
        }

        if (tabIndex == 0) {
            toFetchFragment.filter(String.valueOf(object));
        } else if (tabIndex == 1) {
            deliveringFragment.filter(String.valueOf(object));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Subscribe
    public void onUpdateDeliverFragmentTabOrderCount(UpdateDeliverFragmentTabOrderCount event) {
        TabLayout.Tab tab = tabLayout.getTabAt(event.tabIndex);
        if (event.count > 0) {
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex) + "(" + event.count + ")");
        } else {
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex));
        }

    }

    @Subscribe
    public void onUpdateDeliverOrderDetailEvent(UpdateDeliverOrderDetailEvent event) {
        mOrder = event.orderBean;
        updateDetailView(mOrder);

    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls", "MainDeliverFragment-onPause");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("xls","position = " + tab.getPosition());
        tabIndex = tab.getPosition();
        viewPager.setCurrentItem(tabIndex, false);
        if(tabIndex==0){
            detailLayout.setVisibility(View.GONE);
        }else{
            detailLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void updateDetailView(final OrderBean order) {
        if (order == null) {
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
            itemsContainerLayout.removeAllViews();
            assignBtn.setVisibility(View.GONE);
            reportIssueBtn.setVisibility(View.GONE);
            printOrderBtn.setEnabled(false);
        } else {
            reportIssueBtn.setVisibility(View.VISIBLE);
            printOrderBtn.setEnabled(true);

            orderIdTxt.setText(OrderHelper.getShopOrderSn(order));
            wholeOrderText.setText(order.getOrderSn());
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));

            //服务时效
            reachTimeTxt.setText(OrderHelper.getTimeToService(order));
            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            if (order.getDeliveryTeam() == DeliveryTeam.HAIKUI) {
                deliverNameTxt.setText(order.getCourierName()+"(海葵配送)");
                deliverPhoneTxt.setText(order.getCourierPhone());
            } else if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
                deliverNameTxt.setText("美团配送");
                deliverPhoneTxt.setText("");
            } else {
                deliverNameTxt.setText(order.getCourierName());
                deliverPhoneTxt.setText(order.getCourierPhone());
            }
            fillItemListData(itemsContainerLayout, order);
            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());

            if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN || order.getStatus() == OrderStatus.DELIVERING) {
                assignBtn.setVisibility(View.GONE);
            } else {
                assignBtn.setVisibility(View.VISIBLE);
                if (order.getStatus() == OrderStatus.UNASSIGNED) {
                    assignBtn.setText("订单指派");
                } else if (order.getStatus() == OrderStatus.ASSIGNED) {
                    assignBtn.setText("订单撤回");
                }
            }

            if (OrderHelper.isPrinted(mContext, order.getOrderSn())) {
                printOrderBtn.setText(R.string.print_again);
                printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
            } else {
                printOrderBtn.setText(R.string.print);
                printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
            }
        }

    }

    //填充item数据
    private void fillItemListData(LinearLayout ll, final OrderBean order) {
        if (order == null) {
            return;
        }
        ll.removeAllViews();
        List<ItemContentBean> items = order.getItems();
        for (ItemContentBean item : items) {
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
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.leftMargin = OrderHelper.dip2Px(2, mContext);
            tv1.setLayoutParams(lp1);
            rl.addView(tv1);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.rightMargin = OrderHelper.dip2Px(2, mContext);
            tv2.setLayoutParams(lp2);
            rl.addView(tv2);

            String dingzhi = OrderHelper.getLabelStr(item.getRecipeFittingsList());
            if (!TextUtils.isEmpty(dingzhi)) {
                TextView tv5 = new TextView(mContext);
                tv5.setId(R.id.item_flag);
                tv5.setText(dingzhi);
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(), R.mipmap.flag_ding);
                drawable.setBounds(0, 1, OrderHelper.dip2Px(14, mContext), OrderHelper.dip2Px(14, mContext));
                tv5.setCompoundDrawablePadding(OrderHelper.dip2Px(4, mContext));
                tv5.setCompoundDrawables(drawable, null, null, null);
                tv5.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
                tv5.setTextColor(mContext.getResources().getColor(R.color.font_black));
                RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp5.addRule(RelativeLayout.BELOW, tv1.getId());
                lp5.addRule(RelativeLayout.ALIGN_LEFT, tv1.getId());
                tv5.setLayoutParams(lp5);
                rl.addView(tv5);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(2, mContext);
            ll.addView(rl, lp);
        }
        if (!TextUtils.isEmpty(order.getWishes())) {
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
            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp3.leftMargin = OrderHelper.dip2Px(2, mContext);
            tv3.setLayoutParams(lp3);
            r2.addView(tv3);

            RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp4.rightMargin = OrderHelper.dip2Px(2, mContext);
            tv4.setLayoutParams(lp4);
            r2.addView(tv4);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = OrderHelper.dip2Px(4, mContext);
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
        lp6.topMargin = OrderHelper.dip2Px(30, mContext);
        tv6.setLayoutParams(lp6);
        ll.addView(tv6, lp6);

        ll.invalidate();
    }


    @OnClick({R.id.contant_issue_feedback, R.id.btn_assign, R.id.ll_user_remark, R.id.ll_csad_remark, R.id.btn_print_order})
    public void onClick(View view) {
        if(mOrder==null){
            return;
        }
        switch (view.getId()) {
            case R.id.contant_issue_feedback:
                //问题反馈
                ReportIssueDialog rid = ReportIssueDialog.newInstance(mOrder.getId());
                rid.show(getChildFragmentManager(), "report_issue");
                break;
            case R.id.btn_assign:
                if (mOrder.getStatus() == OrderStatus.UNASSIGNED){
                    Intent intent = new Intent(mContext, AssignOrderActivity.class);
                    intent.putExtra("orderId", mOrder.getId());
                    mContext.startActivity(intent);
                }else if(mOrder.getStatus() == OrderStatus.ASSIGNED){
                    mMainDeliverPresenter.reqRecallOrder(getActivity(),mOrder.getId());
                }
                break;
            case R.id.ll_user_remark:
                //用户备注
                new InfoDetailDialog(getActivity()).show(userRemarkTxt.getText().toString());
                break;
            case R.id.ll_csad_remark:
                //客服备注
                new InfoDetailDialog(getActivity()).show(csadRemarkTxt.getText().toString());
                break;
            case R.id.btn_print_order:
                EventBus.getDefault().post(new PrintOrderEvent(mOrder));
                break;
        }
    }


    public interface FilterOrdersListenter {
        void filter(String category);
    }

}
