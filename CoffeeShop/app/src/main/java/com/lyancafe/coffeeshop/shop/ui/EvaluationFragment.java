package com.lyancafe.coffeeshop.shop.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenter;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EvaluationFragment extends BaseFragment implements EvaluationView<EvaluationBean> {

    private static String TAG = EvaluationFragment.class.getName();
    @BindView(R.id.pmrv_evaluation_list) PullLoadMoreRecyclerView pmrvEvaluationList;

    private EvaluationListAdapter mAdapter;
    private int mLastFeedbackId = 1;

    private Unbinder unbinder;

    private Handler mHandler;
    private EvaluationTaskRunnable mRunnable;
    private EvaluationPresenter mEvaluationPresenter;

    public EvaluationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mEvaluationPresenter = new EvaluationPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mAdapter = new EvaluationListAdapter(this);
        pmrvEvaluationList.setGridLayout(4);
        pmrvEvaluationList.setAdapter(mAdapter);
        pmrvEvaluationList.getRecyclerView().setHasFixedSize(true);
        pmrvEvaluationList.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        pmrvEvaluationList.getRecyclerView().addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(8, getContext()), false));
        pmrvEvaluationList.setPullRefreshEnable(false);
        pmrvEvaluationList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                Log.d(TAG,"mLastFeedbackId = "+ mLastFeedbackId);
                mEvaluationPresenter.loadEvaluations(mLastFeedbackId,true);
            }
        });
    }

    @Override
    public void bindDataToView(List<EvaluationBean> list) {
        mAdapter.setData(list);
    }


    @Override
    public void appendListData(List<EvaluationBean> list) {
        mAdapter.addData(list);
    }

    @Override
    public void saveLastFeedbackId() {
        if(mAdapter.list.size()>0){
            Collections.sort(mAdapter.list, new Comparator<EvaluationBean>() {
                @Override
                public int compare(EvaluationBean o1, EvaluationBean o2) {
                    return o1.getId()-o2.getId();
                }
            });
            mLastFeedbackId = mAdapter.list.get(mAdapter.list.size()-1).getId();
        }else {
            mLastFeedbackId = 1;
        }
    }

    @Override
    public void stopLoadingProgress() {
        pmrvEvaluationList.setPullLoadMoreCompleted();
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
        Log.d("xls","EvaluationFragment  Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new EvaluationTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        Log.d("xls","EvaluationFragment  InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    class EvaluationTaskRunnable implements Runnable{
        @Override
        public void run() {
            mEvaluationPresenter.loadEvaluations(1,false);
        }
    }
}
