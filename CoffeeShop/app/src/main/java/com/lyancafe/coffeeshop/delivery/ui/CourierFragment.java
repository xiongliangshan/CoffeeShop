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
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CourierFragment extends BaseFragment implements CourierView,CourierRvAdapter.MapListener{

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
        mCourierPresenter.loadCouriers();
    }

    @Override
    public void bindDataToListView(List<CourierBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    private void initView(){
        rvCourierList.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvCourierList.setHasFixedSize(true);
        rvCourierList.addItemDecoration(new SpaceItemDecoration(2, OrderHelper.dip2Px(96, getActivity()), false));
        mAdapter = new CourierRvAdapter(this);
        rvCourierList.setAdapter(mAdapter);
    }

    @Override
    public void showMap(double lat, double lng) {
        MapDialog mapDialog = MapDialog.newInstance(lat,lng);
        mapDialog.show(getChildFragmentManager(),"map");
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
            mCourierPresenter.loadCouriers();
        }
    }
}
