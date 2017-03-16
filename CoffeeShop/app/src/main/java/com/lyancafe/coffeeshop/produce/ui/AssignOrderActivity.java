package com.lyancafe.coffeeshop.produce.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.produce.model.DeliverBean;
import com.lyancafe.coffeeshop.produce.presenter.AssignOrderPresenter;
import com.lyancafe.coffeeshop.produce.presenter.AssignOrderPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.AssignOrderView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by Administrator on 2016/7/19.
 */
public class AssignOrderActivity extends Activity implements AssignOrderView{

    private static final String TAG = "AssignOrderActivity";
    private Context mContext;
    @BindView(R.id.spinner_courier) Spinner courierSpinner;
    private CourierListAdapter mAdapter;
    private DeliverBean mCourier = null;
    private long mOrderId = 0;
    private AssignOrderPresenter mAssignOrderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_assign_order);
        ButterKnife.bind(this);
        mOrderId = getIntent().getLongExtra("orderId",0);
        mAssignOrderPresenter = new AssignOrderPresenterImpl(this,this);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAssignOrderPresenter.loadDelivers();
    }


    @OnItemSelected(value = R.id.spinner_courier,callback = OnItemSelected.Callback.ITEM_SELECTED)
    void onItemSelected(int position){
        mCourier = (DeliverBean) mAdapter.getItem(position);
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
            mAssignOrderPresenter.assignOrder(mOrderId,mCourier.getUserId());
        }
    }


    @Override
    public void addDeliversToList(List<DeliverBean> courierBeanList) {
        mAdapter.setData(courierBeanList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
