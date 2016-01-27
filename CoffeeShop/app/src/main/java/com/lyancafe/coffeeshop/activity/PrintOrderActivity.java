package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.PrintHelpter;
import com.lyancafe.coffeeshop.utils.ToastUtil;

/**
 * Created by Administrator on 2016/1/27.
 */
public class PrintOrderActivity extends Activity implements View.OnClickListener,PrintHelpter.OnPromptListener{

    private static final String TAG = "PrintOrderActivity";
    private Button printBoxBtn;
    private Button printCupBtn;
    private Button checkPrinterBtn;
    private OrderBean mOrderBean;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_print_order);
        initView();
        PrintHelpter.getInstance().setPromptlistener(this);
        mOrderBean = (OrderBean) getIntent().getSerializableExtra("order");
    }
    private void initView(){
        printBoxBtn = (Button) findViewById(R.id.btn_print_box);
        printBoxBtn.setOnClickListener(this);
        printCupBtn = (Button) findViewById(R.id.btn_print_cup);
        printCupBtn.setOnClickListener(this);
        checkPrinterBtn = (Button) findViewById(R.id.btn_check_printer);
        checkPrinterBtn.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_print_box:
                PrintHelpter.getInstance().printOrderInfo(mOrderBean);
                break;
            case R.id.btn_print_cup:
                PrintHelpter.getInstance().printOrderItems(mOrderBean);
                break;
            case R.id.btn_check_printer:
                PrintHelpter.getInstance().checkPrinterStatus();
                break;
        }
    }

    @Override
    public void onPrompt(int type, String message) {
        ToastUtil.showToast(mContext,message);
    }
}
