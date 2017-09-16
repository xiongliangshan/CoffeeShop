package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.base.PrintOrderActivity;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderCategory;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.event.UpdateTabCount;
import com.lyancafe.coffeeshop.produce.presenter.MainProducePresenter;
import com.lyancafe.coffeeshop.produce.presenter.MainProducePresenterImpl;
import com.lyancafe.coffeeshop.produce.view.MainProduceView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.widget.InfoDetailDialog;
import com.lyancafe.coffeeshop.widget.ReportIssueDialog;
import com.lyancafe.coffeeshop.widget.UnderLineTextView;
import com.lyancafe.coffeeshop.widget.ZoomOutPageTransformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2015/9/1.
 */
public class MainProduceFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, MainProduceView {

    private static final String TAG = "MainProduceFragment";
    @BindView(R.id.tv_relationOrderId)
    TextView tvRelationOrderId;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.cl_replenish_layout)
    ConstraintLayout clReplenishLayout;
    private Context mContext;
    public static int tabIndex = 0;
    public static int category = OrderCategory.ALL;
    private static final int REQUEST_ASSIGN = 0x1001;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.vp_container)
    ViewPager viewPager;
    @BindView(R.id.order_id)
    TextView shopOrderNoText;
    @BindView(R.id.reach_time)
    TextView reachTimeTxt;
    @BindView(R.id.receiver_name)
    TextView receiveNameTxt;
    @BindView(R.id.receiver_phone)
    TextView receivePhoneTxt;
    @BindView(R.id.order_time)
    TextView orderTimeTxt;
    @BindView(R.id.tv_whole_order_sn)
    TextView orderIdText;
    @BindView(R.id.receiver_address)
    TextView receiveAddressTxt;
    @BindView(R.id.tv_deliver_team)
    TextView deliverTeamText;
    @BindView(R.id.deliver_name)
    TextView deliverNameTxt;
    @BindView(R.id.deliver_phone)
    TextView deliverPhoneTxt;
    @BindView(R.id.items_container_layout)
    LinearLayout itemsContainerLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.user_remark)
    TextView userRemarkTxt;
    @BindView(R.id.ll_user_remark)
    LinearLayout userRemarkLayout;
    @BindView(R.id.csad_remark)
    TextView csadRemarkTxt;
    @BindView(R.id.ll_csad_remark)
    LinearLayout csadRemarkLayout;
    @BindView(R.id.btn_finish_produce)
    Button finishProduceBtn;
    @BindView(R.id.btn_print_order)
    Button printOrderBtn;
    @BindView(R.id.ll_twobtn)
    LinearLayout twoBtnLayout;
    @BindView(R.id.btn_produce_print)
    Button produceAndPrintBtn;
    @BindView(R.id.ll_onebtn)
    LinearLayout oneBtnLayout;
    @BindView(R.id.contant_issue_feedback)
    UnderLineTextView reportIssueBtn;


    private ProduceFragmentPagerAdapter mPagerAdapter;

    private ToProduceFragment toProduceFragment;
    private ProducingFragment producingFragment;
    private ProducedFragment producedFragment;
    private FinishedOrderFragment finishedOrderFragment;
    private RevokedFragment revokedFragment;
    private TomorrowFragment tomorrowFragment;

    private OrderBean mOrder = null;
    private Unbinder unbinder;
    private MainProducePresenter mMainProducePresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Log.d(TAG, "onAttach");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mMainProducePresenter = new MainProducePresenterImpl(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        EventBus.getDefault().register(this);
        return contentView;
    }


    private void initViews() {
        List<Fragment> fragments = new ArrayList<>();
        toProduceFragment = new ToProduceFragment();
        producingFragment = new ProducingFragment();
        producedFragment = new ProducedFragment();
        finishedOrderFragment = new FinishedOrderFragment();
        revokedFragment = new RevokedFragment();
        tomorrowFragment = new TomorrowFragment();
        fragments.add(toProduceFragment);
        fragments.add(producingFragment);
        fragments.add(producedFragment);
        fragments.add(finishedOrderFragment);
        fragments.add(revokedFragment);
        fragments.add(tomorrowFragment);
        mPagerAdapter = new ProduceFragmentPagerAdapter(getChildFragmentManager(), getActivity(), fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

    }


    @Override
    public void refreshListForStatus(long orderId, int status) {
        int currentFragmentIndex = tabLayout.getSelectedTabPosition();
        switch (currentFragmentIndex) {
            case 0:
                toProduceFragment.refreshListForStatus(orderId, status);
                break;
            case 1:
                producingFragment.refreshListForStatus(orderId, status);
                break;
        }
    }

    private void updateDetailView(final OrderBean order) {
        if (order == null) {
            shopOrderNoText.setText("");
            orderIdText.setText("");
            orderTimeTxt.setText("");
            reachTimeTxt.setText("");
            receiveNameTxt.setText("");
            receivePhoneTxt.setText("");
            receiveAddressTxt.setText("");
            deliverTeamText.setText("");
            deliverNameTxt.setText("");
            deliverPhoneTxt.setText("");
            userRemarkTxt.setText("");
            csadRemarkTxt.setText("");
            itemsContainerLayout.removeAllViews();
            reportIssueBtn.setVisibility(View.GONE);
            produceAndPrintBtn.setEnabled(false);
            finishProduceBtn.setEnabled(false);
            printOrderBtn.setEnabled(false);
            clReplenishLayout.setVisibility(View.GONE);
        } else {
            reportIssueBtn.setVisibility(View.VISIBLE);
            produceAndPrintBtn.setEnabled(true);
            finishProduceBtn.setEnabled(true);
            printOrderBtn.setEnabled(true);

            shopOrderNoText.setText(OrderHelper.getShopOrderSn(order));
            orderIdText.setText(String.valueOf(order.getId()));
            orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
            if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
                reachTimeTxt.setText(order.getInstant() == 1 ? "立即送出" : OrderHelper.getDateToString(order.getExpectedTime()));
            } else {
                reachTimeTxt.setText(order.getInstant() == 1 ? "尽快送达" : OrderHelper.getDateToMonthDay(order.getExpectedTime()));
            }

            long relationOrderId = order.getRelationOrderId();
            if(relationOrderId!=0){
                clReplenishLayout.setVisibility(View.VISIBLE);
                tvRelationOrderId.setText(String.valueOf(relationOrderId));
                tvReason.setText(order.getReason());
            }else {
                clReplenishLayout.setVisibility(View.GONE);
            }

            receiveNameTxt.setText(order.getRecipient());
            receivePhoneTxt.setText(order.getPhone());
            receiveAddressTxt.setText(order.getAddress());
            if (order.getDeliveryTeam() == DeliveryTeam.HAIKUI) {
                deliverTeamText.setText("(海葵)");
            } else if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
                deliverTeamText.setText("(美团)");
            } else {
                deliverTeamText.setText("(自有)");
            }
            deliverNameTxt.setText(order.getCourierName());
            deliverPhoneTxt.setText(order.getCourierPhone());

            fillItemListData(itemsContainerLayout, order);

            userRemarkTxt.setText(order.getNotes());
            csadRemarkTxt.setText(order.getCsrNotes());

            if (order.getRevoked()) {
                twoBtnLayout.setVisibility(View.GONE);
                oneBtnLayout.setVisibility(View.GONE);
            } else {
                if (order.getProduceStatus() == OrderStatus.UNPRODUCED) {
                    twoBtnLayout.setVisibility(View.GONE);
                    oneBtnLayout.setVisibility(View.VISIBLE);
                    if (OrderHelper.isTomorrowOrder(order)) {
                        produceAndPrintBtn.setVisibility(View.GONE);
                    } else {
                        produceAndPrintBtn.setVisibility(View.VISIBLE);
                    }
                } else if (order.getProduceStatus() == OrderStatus.PRODUCING) {
                    twoBtnLayout.setVisibility(View.VISIBLE);
                    oneBtnLayout.setVisibility(View.GONE);
                    finishProduceBtn.setVisibility(View.VISIBLE);
                    if (OrderHelper.isPrinted(mContext, order.getOrderSn())) {
                        printOrderBtn.setText(R.string.print_again);
                        printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.red1));
                    } else {
                        printOrderBtn.setText(R.string.print);
                        printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.white1));
                    }
                } else if (order.getProduceStatus() == OrderStatus.PRODUCED) {
                    twoBtnLayout.setVisibility(View.VISIBLE);
                    oneBtnLayout.setVisibility(View.GONE);
                    finishProduceBtn.setVisibility(View.GONE);
                    if (OrderHelper.isPrinted(mContext, order.getOrderSn())) {
                        printOrderBtn.setText(R.string.print_again);
                        printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_red));
                    } else {
                        printOrderBtn.setText(R.string.print);
                        printOrderBtn.setTextColor(mContext.getResources().getColor(R.color.text_black));
                    }
                } else {
                    twoBtnLayout.setVisibility(View.GONE);
                    oneBtnLayout.setVisibility(View.GONE);
                }

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
            tv1.setTextColor(mContext.getResources().getColor(R.color.black2));
            TextView tv2 = new TextView(mContext);
            tv2.setId(R.id.item_num);
            tv2.setText("x  " + item.getQuantity());
            tv2.getPaint().setFakeBoldText(true);
            tv2.setTextSize(mContext.getResources().getDimension(R.dimen.content_item_text_size));
            tv2.setTextColor(mContext.getResources().getColor(R.color.black2));

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

            String dingzhi = item.getRecipeFittings();
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
            lp.bottomMargin = OrderHelper.dip2Px(4, mContext);
            ll.addView(rl, lp);
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


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabIndex = tab.getPosition();
        viewPager.setCurrentItem(tabIndex, false);
        LogUtil.d(LogUtil.TAG_PRODUCE, "当前fragment: tabIndex =" + tabIndex);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 更新tab数量显示
     *
     * @param event
     */
    @Subscribe
    public void OnUpdateTabCountEvent(UpdateTabCount event) {
        LogUtil.d(TAG, "UpdateTabCount : index = " + event.tabIndex + " | count = " + event.count);
        TabLayout.Tab tab = tabLayout.getTabAt(event.tabIndex);
        tab.setTag(event.count);
        if (event.count > 0) {
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex) + "(" + event.count + ")");
        } else {
            tab.setText(mPagerAdapter.getPageTitle(event.tabIndex));
        }
        LogUtil.d(TAG, tab.toString() + tab.getPosition() + " getTag = " + tab.getTag());
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:");
        if (toProduceFragment != null) {
            toProduceFragment.onVisible();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
        Log.d(TAG, "onDestroyView");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }


    /**
     * 对订单操作后更改相关列表中的订单数量角标显示
     *
     * @param event
     */
    @Subscribe
    public void onChangeTabCountByActionEvent(ChangeTabCountByActionEvent event) {
        LogUtil.d(TAG, "onChangeTabCountByAction :action = " + event.action + " | count = " + event.count);
        TabLayout.Tab tabToproduce = tabLayout.getTabAt(0);
        TabLayout.Tab tabProducing = tabLayout.getTabAt(1);
        TabLayout.Tab tabProduced = tabLayout.getTabAt(2);
        int toProduceCount = tabToproduce.getTag() == null ? 0 : (Integer) tabToproduce.getTag();
        int producingCount = tabProducing.getTag() == null ? 0 : (Integer) tabProducing.getTag();
        int producedCount = tabProduced.getTag() == null ? 0 : (Integer) tabProduced.getTag();
        switch (event.action) {
            case OrderAction.STARTPRODUCE:
                int tabTo = toProduceCount - event.count;
                int tabPro = event.isQrCode ? producingCount : producingCount + event.count;
                if (tabTo < 0) {
                    tabTo = 0;
                }
                if (tabPro < 0) {
                    tabPro = 0;
                }
                tabToproduce.setText(tabTo <= 0 ? mPagerAdapter.getPageTitle(0) : mPagerAdapter.getPageTitle(0) + "(" + tabTo + ")");
                tabProducing.setText(tabPro <= 0 ? mPagerAdapter.getPageTitle(1) : mPagerAdapter.getPageTitle(1) + "(" + tabPro + ")");
                tabToproduce.setTag(tabTo);
                tabProducing.setTag(tabPro);
                break;
            case OrderAction.FINISHPRODUCE:
                int tabProduceResult = producingCount - event.count;
                int tabProducedResult = producedCount + event.count;
                if (tabProduceResult < 0) {
                    tabProduceResult = 0;
                }
                if (tabProducedResult < 0) {
                    tabProducedResult = 0;
                }
                tabProducing.setText(tabProduceResult <= 0 ? mPagerAdapter.getPageTitle(1) : mPagerAdapter.getPageTitle(1) + "(" + tabProduceResult + ")");
                tabProduced.setText(tabProducedResult <= 0 ? mPagerAdapter.getPageTitle(2) : mPagerAdapter.getPageTitle(2) + "(" + tabProducedResult + ")");
                tabProducing.setTag(tabProduceResult);
                tabProduced.setTag(tabProducedResult);
                break;
            case OrderAction.REVOKEORDER:
                TabLayout.Tab tab = tabLayout.getTabAt(event.tabIndex);
                int count = tab.getTag() == null ? 0 : (Integer) tab.getTag();
                count = count - 1;
                if (count < 0) {
                    count = 0;
                }
                tab.setTag(count);
                String tabTitle = mPagerAdapter.getPageTitle(event.tabIndex).toString();
                tab.setText(count == 0 ? tabTitle : tabTitle + "(" + count + ")");
                break;
            case OrderAction.NONEEDPRODUCE:
                int tabToProduceCount = toProduceCount - event.count;
                int tabProducedCount = producedCount + event.count;
                if (tabToProduceCount < 0) {
                    tabToProduceCount = 0;
                }
                if (tabProducedCount < 0) {
                    tabProducedCount = 0;
                }
                tabToproduce.setText(tabToProduceCount <= 0 ? mPagerAdapter.getPageTitle(0) : mPagerAdapter.getPageTitle(0) + "(" + tabToProduceCount + ")");
                tabProduced.setText(tabProducedCount <= 0 ? mPagerAdapter.getPageTitle(2) : mPagerAdapter.getPageTitle(2) + "(" + tabProducedCount + ")");
                tabToproduce.setTag(tabToProduceCount);
                tabProduced.setTag(tabProducedCount);
                break;
        }
    }


    /**
     * 单独点击打印按钮事件
     *
     * @param event
     */
    @Subscribe
    public void onPrintOrderEvent(PrintOrderEvent event) {
        printOrder(mContext, event.order);
    }


    @Subscribe
    public void onUpdateOrderDetailEvent(UpdateOrderDetailEvent event) {
        mOrder = event.orderBean;
        updateDetailView(mOrder);
        LogUtil.d(LogUtil.TAG_PRODUCE, "getChildFragment:" + tabLayout.getSelectedTabPosition());
    }

    //更新详情面板
    public void updateOrderDetail(OrderBean orderBean) {
        mOrder = orderBean;
        updateDetailView(mOrder);
    }

    @OnClick({R.id.contant_issue_feedback,R.id.ll_user_remark, R.id.ll_csad_remark, R.id.btn_finish_produce, R.id.btn_print_order, R.id.btn_produce_print})
    public void onClick(View view) {
        if (mOrder == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.contant_issue_feedback:
                //问题反馈
                ReportIssueDialog rid = ReportIssueDialog.newInstance(mOrder.getId());
                rid.show(getChildFragmentManager(), "report_issue");
                break;
            case R.id.ll_user_remark:
                //用户备注
                new InfoDetailDialog(getActivity()).show(userRemarkTxt.getText().toString());
                break;
            case R.id.ll_csad_remark:
                //客服备注
                new InfoDetailDialog(getActivity()).show(csadRemarkTxt.getText().toString());
                break;
            case R.id.btn_finish_produce:
                //生产完成
                EventBus.getDefault().post(new FinishProduceEvent(mOrder));
                break;
            case R.id.btn_print_order:
                EventBus.getDefault().post(new PrintOrderEvent(mOrder));
                break;
            case R.id.btn_produce_print:
                //点击开始生产（打印）按钮
                EventBus.getDefault().post(new StartProduceEvent(mOrder));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ASSIGN && resultCode == 1) {
            long orderId = data.getLongExtra("orderId", 0L);
            if (orderId != 0) {
                refreshListForStatus(orderId, OrderStatus.ASSIGNED);
            }
        }
    }

    //点击打印
    private void printOrder(Context context, final OrderBean order) {
        //打印按钮
        Intent intent = new Intent(context, PrintOrderActivity.class);
        intent.putExtra("order", order);
        context.startActivity(intent);
    }

}
