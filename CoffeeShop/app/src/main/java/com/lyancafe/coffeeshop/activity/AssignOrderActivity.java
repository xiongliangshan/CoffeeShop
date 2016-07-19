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
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_assign_order);
        initViews();

        mAdapter = new CourierListAdapter(mContext);
        courierSpinner.setAdapter(mAdapter);
        courierSpinner.setSelection(0, true);
        mAdapter.setData(createTestData());


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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assignBtn = (Button) findViewById(R.id.btn_assign);
        assignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"点击指派按钮");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }


    private List<CourierBean> createTestData(){
        List<CourierBean> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            CourierBean courierBean = new CourierBean(i+1,"我是第"+(i+1)+"个",1,false,1600,4);
            list.add(courierBean);
        }
        return list;
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
        }
    }
}
