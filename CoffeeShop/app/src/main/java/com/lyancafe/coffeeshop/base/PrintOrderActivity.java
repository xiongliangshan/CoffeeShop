package com.lyancafe.coffeeshop.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/27.
 */
public class PrintOrderActivity extends Activity implements PrintHelper.OnPromptListener {

    private static final String TAG = "PrintOrderActivity";
    private OrderBean mOrderBean;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_print_order);
        ButterKnife.bind(this);
        PrintHelper.getInstance().setPromptlistener(this);
        mOrderBean = (OrderBean) getIntent().getSerializableExtra("order");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_print_box, R.id.btn_print_cup, R.id.btn_print_all, R.id.btn_check_printer})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print_box:
                PrintHelper.getInstance().printOrderInfo(mOrderBean);
                break;
            case R.id.btn_print_cup:
                PrintHelper.getInstance().printOrderItems(mOrderBean);
                break;
            case R.id.btn_print_all:
                PrintHelper.getInstance().printOrderInfo(mOrderBean);
                PrintHelper.getInstance().printOrderItems(mOrderBean);
                break;
            case R.id.btn_check_printer:
                PrintHelper.getInstance().checkPrinterStatus();
                break;
        }
    }

    @Override
    public void onPrompt(int type, String message) {
        ToastUtil.showToast(mContext, message);
    }

}
