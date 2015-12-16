package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/20.
 */
public class ReportWindow extends PopupWindow implements View.OnClickListener{

    private static final String TAG = "ReportWindow";
    private View contentView;
    private TextView orderIdTxt;
    private TextView orderTimeTxt;
    private Spinner typeSpinner;
    private Spinner ideaSpinner;
    private EditText descriptionEdt;
    private Button okBtn;
    private Button cacelBtn;
    private OrderBean orderBean;
    private Context mContext;
    private static int  QUESTION_TYPE = 17; //14.产线繁忙 15.无法生产 16.定位错误，应属其他区域 17.其他
    private static int  QUESTION_IDEA = 1; //1.取消订单 2.改约时间
    private static String QUESTION_DESCRIPTION ="";
    private Handler handler;

    public ReportWindow(Context context,Handler handler) {
        super(context);
        mContext = context;
        this.handler = handler;
        contentView = LayoutInflater.from(context).inflate(R.layout.window_popup_report,null);
        this.setContentView(contentView);
        initView(contentView,mContext);
        this.setHeight(OrderHelper.dip2Px(600, context));
        this.setWidth(OrderHelper.dip2Px(444, context));
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.report_popwin_anim_style);
    }

    public void setOrder(OrderBean orderBean){
        this.orderBean = orderBean;
        updateWindowData(this.orderBean);
    }

    //更新数据
    private void updateWindowData(OrderBean order){
        orderIdTxt.setText(order.getOrderSn());
        orderTimeTxt.setText(OrderHelper.getDateToString(order.getOrderTime()));
    }
    private void initSpinner(View contentView,final Context context){
        typeSpinner = (Spinner) contentView.findViewById(R.id.report_question_type);
        ideaSpinner = (Spinner) contentView.findViewById(R.id.report_question_idea);

        final ArrayAdapter< String> adapter_type = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_type.add(context.getResources().getString(R.string.question_type));
        adapter_type.add(context.getResources().getString(R.string.shop_busy));
        adapter_type.add(context.getResources().getString(R.string.produce_problem));
        adapter_type.add(context.getResources().getString(R.string.location_error));
        adapter_type.add(context.getResources().getString(R.string.other));
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter_type);
        typeSpinner.setSelection(0, true);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        QUESTION_TYPE = 17;
                        break;
                    case 1:
                        QUESTION_TYPE = 14;
                        break;
                    case 2:
                        QUESTION_TYPE = 15;
                        break;
                    case 3:
                        QUESTION_TYPE = 16;
                        break;
                    case 4:
                        QUESTION_TYPE = 17;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter< String> adapter_idea = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_idea.add(context.getResources().getString(R.string.question_idea));
        adapter_idea.add(context.getResources().getString(R.string.cancel_order));
        adapter_idea.add(context.getResources().getString(R.string.modify_order_time));
        adapter_idea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ideaSpinner.setAdapter(adapter_idea);
        ideaSpinner.setSelection(0, true);
        ideaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        QUESTION_IDEA = 1;
                        break;
                    case 1:
                        QUESTION_IDEA = 1;
                        break;
                    case 2:
                        QUESTION_IDEA = 2;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initView(View contentView,Context context){
        initSpinner(contentView,context);
        orderIdTxt = (TextView) contentView.findViewById(R.id.report_order_id);
        orderTimeTxt = (TextView) contentView.findViewById(R.id.report_order_time);
        descriptionEdt = (EditText) contentView.findViewById(R.id.report_order_description);
        okBtn = (Button) contentView.findViewById(R.id.report_ok);
        cacelBtn = (Button) contentView.findViewById(R.id.report_cancel);

        okBtn.setOnClickListener(this);
        cacelBtn.setOnClickListener(this);

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showReportWindow(View parent) {
        if (!this.isShowing()) {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_ok:
                QUESTION_DESCRIPTION = descriptionEdt.getText().toString();
                new ReportOrderQry(mContext,orderBean.getId(),QUESTION_TYPE,QUESTION_IDEA,QUESTION_DESCRIPTION).doRequest();
                break;
            case R.id.report_cancel:
                this.dismiss();
                break;
        }
    }

    //报告问题订单接口
    class ReportOrderQry implements Qry {

        private Context context;
        private long orderId;
        private int questionType;
        private int questionIdea;
        private String questionDesc;

        public ReportOrderQry(Context context, long orderId, int questionType, int questionIdea, String questionDesc) {
            this.context = context;
            this.orderId = orderId;
            this.questionType = questionType;
            this.questionIdea = questionIdea;
            this.questionDesc = questionDesc;
        }

        @Override
        public void doRequest() {

            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/raiseissue?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("questionType",questionType);
            params.put("handleType",questionIdea);
            params.put("remark", questionDesc);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "ReportOrderQry:resp =" + resp);
            if(resp == null){
                ToastUtil.showToast(context, R.string.unknown_error);
                ReportWindow.this.dismiss();
                return;
            }
            if(resp.status==0){
                ToastUtil.showToast(context, R.string.do_success);
                Message msg = new Message();
                msg.what = OrdersFragment.MSG_UPDATE_QUESTION_ORDER_FLAG;
                Bundle bundle = new Bundle();
                bundle.putLong("orderId",orderId);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }else{
                ToastUtil.showToast(context, resp.message);
            }
            ReportWindow.this.dismiss();
        }


    }

}
