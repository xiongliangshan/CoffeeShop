package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.CourierListAdapter;
import com.lyancafe.coffeeshop.bean.CourierBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/19.
 */
public class AssignOrderActivity extends Activity  {

    private static final String TAG = "AssignOrderActivity";
    private Context mContext;
    private Spinner courierSpinner;
    private CourierListAdapter mAdapter;
    private Button assignBtn;
    private CourierBean mCourier = null;
    private long mOrderId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_assign_order);
        mOrderId = getIntent().getLongExtra("orderId",0);
        initViews();

        mAdapter = new CourierListAdapter(mContext);
        courierSpinner.setAdapter(mAdapter);
        courierSpinner.setSelection(0, true);


    }

    @Override
    protected void onStart() {
        super.onStart();
        new CourierListQry().doRequest();
    }

    private void initViews(){
        courierSpinner = (Spinner) findViewById(R.id.spinner_courier);
        courierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCourier = (CourierBean) mAdapter.getItem(position);
                Log.d(TAG,"选择了:"+mCourier.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assignBtn = (Button) findViewById(R.id.btn_assign);
        assignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击指派按钮");
                if(mCourier!=null&&mOrderId!=0){
                    new AssignQry(mOrderId,mCourier.getUserId()).doRequest();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }


    class CourierListQry implements Qry{

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(mContext);
            int shopId = LoginHelper.getShopId(mContext);
            String url = HttpUtils.BASE_URL+shopId+"/couriersforassign?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.GET, url, params), mContext, this, true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"CourierListQry:resp ="+resp);
            if(resp==null){
                Log.e(TAG,"CourierListQry:resp ="+resp);
                return;
            }
            if(resp.status==0){
                List<CourierBean> courierBeanList = CourierBean.parseJsonToCouriers(resp);
                mAdapter.setData(courierBeanList);
            }else{
                ToastUtil.show(mContext,resp.message);
            }
        }
    }

    class AssignQry implements Qry{

        private long orderId;
        private long courierId;

        public AssignQry(long orderId, long courierId) {
            this.orderId = orderId;
            this.courierId = courierId;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(mContext);
            int shopId = LoginHelper.getShopId(mContext);
            String url = HttpUtils.BASE_URL+shopId+"/order/"+orderId+"/assigntocourier?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("courierId",courierId);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), mContext, this, true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"AssignQry:resp = "+resp);
            if(resp==null){
                ToastUtil.show(mContext,"请求服务器失败");
                return;
            }
            if(resp.status==0){
                ToastUtil.show(mContext,"指派成功");
                AssignOrderActivity.this.finish();
            }else{
                ToastUtil.show(mContext,resp.message);
            }
        }
    }
}
