package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.produce.presenter.TomorrowPresenter;
import com.lyancafe.coffeeshop.produce.presenter.TomorrowPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.TomorrowView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/28.
 */

public class TomorrowFragment extends BaseFragment implements TomorrowView<OrderBean> {


    @BindView(R.id.rv_tomorrow)
    RecyclerView rvTomorrow;
    Unbinder unbinder;

    private Context mContext;
    private TomorrowPresenter mPresenter;
    private Handler mHandler;
    private TomorrowTaskRunnable mRunnable;
    private TomorrowRvAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mPresenter = new TomorrowPresenterImpl(getContext(),this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new TomorrowRvAdapter(getActivity());
        rvTomorrow.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        rvTomorrow.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getActivity()), false));
        rvTomorrow.setAdapter(mAdapter);
    }


    @Override
    public void bindDataToView(List<OrderBean> list) {
        mAdapter.setData(list);
    }


    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls","tomorrowFragment is Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new TomorrowTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }


    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls","tomorrowFragment is InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    class TomorrowTaskRunnable implements Runnable{
        @Override
        public void run() {
            mPresenter.loadTomorrowOrders();
        }
    }
}
