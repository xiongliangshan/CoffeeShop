package com.lyancafe.coffeeshop.shop.ui;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.shop.presenter.TimeEffectPresenter;
import com.lyancafe.coffeeshop.shop.presenter.TimeEffectPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.TimeEffectView;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class TimeEffectFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener,TimeEffectView{

    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_bad)
    RadioButton rbBad;
    @BindView(R.id.rb_normal)
    RadioButton rbNormal;
    @BindView(R.id.rb_great)
    RadioButton rbGreat;
    @BindView(R.id.rg_effct_type)
    RadioGroup rgEffctType;
    @BindView(R.id.rv_effectListView)
    PullLoadMoreRecyclerView rvEffectListView;

    private int mLastOrderId = 0;
    private int mType = 0;

    private Unbinder unbinder;


    private TimeEffectListAdapter mAdapter;

    private Handler mHandler;
    private TimeEffectTaskRunnable mRunnable;
    private TimeEffectPresenter mTimeEffectPresenter;

    public TimeEffectFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mTimeEffectPresenter = new TimeEffectPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_effect, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        rgEffctType.check(R.id.rb_all);
        mAdapter = new TimeEffectListAdapter(getContext());
        rvEffectListView.setAdapter(mAdapter);
        rvEffectListView.setLinearLayout();
        rvEffectListView.setRefreshing(false);
        rvEffectListView.setPullRefreshEnable(false);
        rvEffectListView.getRecyclerView().setHasFixedSize(true);
        rvEffectListView.setOnPullLoadMoreListener(this);

    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void bindDataToListView(List<TimeEffectBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Override
    public void appendListData(List<TimeEffectBean> list) {
        mAdapter.addData(list);
    }

    @Override
    public void saveLastOrderId() {
        if(mAdapter.list.size()>0){
            mLastOrderId = mAdapter.list.get(mAdapter.list.size()-1).getOrderId();
        }else {
            mLastOrderId = 0;
        }
    }

    @Override
    public void stopLoadingProgress() {
        rvEffectListView.setPullLoadMoreCompleted();
    }

    @Override
    public void bindTimeEffctAmount(int totalCount, int goodCount, int passedCount, int fallingCount) {
        if(rbAll!=null && totalCount>0){
            rbAll.setText(getContext().getResources().getString(R.string.count_all,totalCount));
        }
        if(rbBad!=null && fallingCount>0){
            rbBad.setText(getContext().getResources().getString(R.string.count_not_passed,fallingCount));
        }
        if(rbNormal!=null && passedCount>0){
            rbNormal.setText(getContext().getResources().getString(R.string.count_passed,passedCount));
        }
        if(rbGreat!=null && goodCount>0){
            rbGreat.setText(getContext().getResources().getString(R.string.count_good,goodCount));
        }
    }

    @Override
    public void onLoadMore() {

        mTimeEffectPresenter.loadTimeEffectList(mLastOrderId,mType);

    }


    @OnClick({R.id.rb_all, R.id.rb_bad, R.id.rb_normal, R.id.rb_great})
    public void onClick(View view) {
        rgEffctType.check(view.getId());
        OkGo.getInstance().cancelTag("timeEffect");
        switch (view.getId()) {
            case R.id.rb_all:
                mType = 0;
                break;
            case R.id.rb_bad:
                mType = 3;
                break;
            case R.id.rb_normal:
                mType = 2;
                break;
            case R.id.rb_great:
                mType = 1;
                break;
        }

        mTimeEffectPresenter.loadTimeEffectList(0,mType);
    }


    class TimeEffectTaskRunnable implements Runnable{
        @Override
        public void run() {
            mTimeEffectPresenter.loadTimeEffectAmount();
            mTimeEffectPresenter.loadTimeEffectList(0,mType);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();
        Log.d("xls","TimeEffectFragment  Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new TimeEffectTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls","TimeEffectFragment  InVisible");
        if(mHandler!=null){
            Log.d("xls","removeCallbacks timeEffect");
            mHandler.removeCallbacks(mRunnable);
        }
    }

}
