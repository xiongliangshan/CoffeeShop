package com.lyancafe.coffeeshop.delivery.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.delivery.model.CourierBean;
import com.lyancafe.coffeeshop.delivery.presenter.CourierPresenter;
import com.lyancafe.coffeeshop.delivery.presenter.CourierPresenterImpl;
import com.lyancafe.coffeeshop.delivery.view.CourierView;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CourierFragment extends BaseFragment implements CourierView{

    @BindView(R.id.rv_courier_list)
    RecyclerView rvCourierList;

    private Unbinder unbinder;
    private CourierRvAdapter mAdapter;

    private CourierPresenter mCourierPresenter;
    private CourierListRunnable mRunnable;
    private Handler mHandler;

    public CourierFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mCourierPresenter = new CourierPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courier, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCourierPresenter.loadCouriersList();
    }

    @Override
    public void addCouriersToList(List<CourierBean> couriers) {
        mAdapter.setData(couriers);
    }

    private void initView(){
        rvCourierList.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvCourierList.setHasFixedSize(true);
        rvCourierList.addItemDecoration(new SpaceItemDecoration(2, OrderHelper.dip2Px(16, getActivity()), false));
        mAdapter = new CourierRvAdapter(createTestData());
        rvCourierList.setAdapter(mAdapter);
    }

    private List<CourierBean> createTestData() {
        List<CourierBean> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            CourierBean d = new CourierBean();
            d.setId(i);
            d.setName("熊"+i);
            d.setPhone("13515454555");
            d.setDistanceToShop(10*i);
            d.setTotalOrderCount(i*5);
            d.setDeliveringOrderCount(i+3);
            list.add(d);
        }
        return list;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        mRunnable = new CourierListRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
    }

    class CourierListRunnable implements Runnable{
        @Override
        public void run() {
            mCourierPresenter.loadCouriersList();
        }
    }
}
