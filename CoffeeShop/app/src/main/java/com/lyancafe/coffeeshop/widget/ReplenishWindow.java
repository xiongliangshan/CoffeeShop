package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ReplenishWindow extends PopupWindow implements View.OnClickListener{

    private static final String TAG = "ReplenishWindow";
    private Context context;
    private Button produceBtn;
    private Button noProduceBtn;
    private OrderBean orderBean;
    private ReplenishCallBack callback;

    public ReplenishWindow(Context context, OrderBean orderBean) {
        super(context);
        this.context = context;
        this.orderBean = orderBean;
        initWindow();
    }


    private void initWindow(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.window_replenish, null);
        setContentView(view);
        setHeight(OrderHelper.dip2Px(100,context));
        setWidth(OrderHelper.dip2Px(180,context));
        setOutsideTouchable(true);
        setAnimationStyle(R.style.window_replenish);
        produceBtn = (Button) view.findViewById(R.id.btn_produce);
        noProduceBtn = (Button) view.findViewById(R.id.btn_no_produce);
        produceBtn.setOnClickListener(this);
        noProduceBtn.setOnClickListener(this);
    }

    public void setCallback(ReplenishCallBack callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_produce:
                if(callback!=null){
                    callback.onProduce(orderBean);
                }
                break;
            case R.id.btn_no_produce:
                if(callback!=null){
                    callback.onNoProduce(orderBean);
                }
                break;
        }
        dismiss();
    }

    public void showPopUpWindow(View anchor){
        Log.d(TAG,"getX = "+anchor.getX()+" | getY = "+anchor.getY()+" | height = "+anchor.getHeight());
        Log.d(TAG,"height = "+getHeight());
        if(!isShowing()){
            showAsDropDown(anchor,0,-(anchor.getHeight()+this.getHeight()));
//            showAtLocation(anchor,Gravity.NO_GRAVITY,(int)anchor.getX(),(int)(anchor.getY()-anchor.getHeight()));
        }
    }



    public void dismissPopUpWindow(){
        if(isShowing()){
            dismiss();
        }
    }

    public interface ReplenishCallBack{

        void onProduce(OrderBean orderBean);
        void onNoProduce(OrderBean orderBean);
    }
}
