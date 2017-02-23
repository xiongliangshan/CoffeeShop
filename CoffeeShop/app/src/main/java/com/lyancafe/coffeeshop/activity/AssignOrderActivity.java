package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.CourierListAdapter;
import com.lyancafe.coffeeshop.bean.CourierBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/19.
 */
public class AssignOrderActivity extends Activity  {

    private static final String TAG = "AssignOrderActivity";
    private Context mContext;
    @BindView(R.id.spinner_courier) Spinner courierSpinner;
    private CourierListAdapter mAdapter;
    private CourierBean mCourier = null;
    private long mOrderId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_assign_order);
        ButterKnife.bind(this);
        mOrderId = getIntent().getLongExtra("orderId",0);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        HttpHelper.getInstance().reqDeliverList(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleDeliverListResponse(xlsResponse, call, response);
            }
        });
    }


    @OnItemSelected(value = R.id.spinner_courier,callback = OnItemSelected.Callback.ITEM_SELECTED)
    void onItemSelected(int position){
        mCourier = (CourierBean) mAdapter.getItem(position);
        Log.d(TAG,"选择了:"+mCourier.toString());
    }



    private void initViews(){
        mAdapter = new CourierListAdapter(mContext);
        courierSpinner.setAdapter(mAdapter);
        courierSpinner.setSelection(0, true);
    }

    @OnClick(R.id.btn_assign)
    void assign(){
        Log.d(TAG, "点击指派按钮");
        if (mCourier != null && mOrderId != 0) {
            HttpHelper.getInstance().reqAssignOrder(mOrderId, mCourier.getUserId(), new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleAssignOrderResponse(xlsResponse,call,response);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    //处理服务器返回数据---小哥列表
    private void handleDeliverListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<CourierBean> courierBeanList = CourierBean.parseJsonToCouriers(xlsResponse);
            mAdapter.setData(courierBeanList);
        }else{
            ToastUtil.show(this,xlsResponse.message);
        }
    }


    //处理服务器返回数据---指派
    private void handleAssignOrderResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            ToastUtil.show(mContext,"指派成功");
            AssignOrderActivity.this.finish();
        }else{
            ToastUtil.show(mContext,xlsResponse.message);
        }
    }

}
