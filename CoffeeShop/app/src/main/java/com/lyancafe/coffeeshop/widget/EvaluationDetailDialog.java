package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.DeliveryTeam;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EvaluationDetailDialog extends DialogFragment {


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
    @BindView(R.id.tv_deliver_phone)
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
    @BindView(R.id.tv_relationOrderId)
    TextView tvRelationOrderId;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.cl_replenish_layout)
    ConstraintLayout clReplenishLayout;

    private OrderBean orderBean;

    private Unbinder unbinder;

    private Context mContext;

    public static EvaluationDetailDialog newInstance(OrderBean orderBean) {

        Bundle args = new Bundle();
        args.putSerializable("order",orderBean);
        EvaluationDetailDialog fragment = new EvaluationDetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
        mContext = getContext();
        Bundle bundle = getArguments();
        if(bundle!=null){
            orderBean = (OrderBean) bundle.getSerializable("order");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_evaluation_detail, container);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        updateDetailView(orderBean);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            Window window = dialog.getWindow();
            window.setLayout((int) (dm.widthPixels * 0.4), (int)(dm.heightPixels * 0.7));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.75f;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void updateDetailView(final OrderBean order) {
        if (order == null) {
            return;
        }

        shopOrderNoText.setText(OrderHelper.getShopOrderSn(order));
        orderIdText.setText(String.valueOf(order.getId()));
        orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
        if (order.getDeliveryTeam() == DeliveryTeam.MEITUAN) {
            reachTimeTxt.setText(order.getInstant() == 1 ? "立即送出" : OrderHelper.getDateToString(order.getExpectedTime()));
        } else {
            reachTimeTxt.setText(order.getInstant() == 1 ? "尽快送达" : OrderHelper.getDateToMonthDay(order.getExpectedTime()));
        }

        long relationOrderId = order.getRelationOrderId();
        if (relationOrderId != 0) {
            clReplenishLayout.setVisibility(View.VISIBLE);
            tvRelationOrderId.setText(String.valueOf(relationOrderId));
            tvReason.setText(order.getReason());
        } else {
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


}
