package com.lyancafe.coffeeshop.shop.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenter;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;
import com.lyancafe.coffeeshop.utils.EvaluationListSortComparator;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EvaluationFragment extends BaseFragment implements EvaluationView<EvaluationBean> {

    private static String TAG = EvaluationFragment.class.getName();
    @BindView(R.id.pmrv_evaluation_list)
    PullLoadMoreRecyclerView pmrvEvaluationList;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_very_good)
    RadioButton rbVeryGood;
    @BindView(R.id.rb_good)
    RadioButton rbGood;
    @BindView(R.id.rb_common)
    RadioButton rbCommon;
    @BindView(R.id.rb_bad)
    RadioButton rbBad;
    @BindView(R.id.rb_very_bad)
    RadioButton rbVeryBad;
    @BindView(R.id.rg_category)
    RadioGroup rgCategory;

    private EvaluationListAdapter mAdapter;
    private int mLastFeedbackId = 1;

    private Unbinder unbinder;

    private Handler mHandler;
    private EvaluationTaskRunnable mRunnable;
    private EvaluationPresenter mEvaluationPresenter;

    private List<EvaluationBean> evaluationBeans;
    private int parameter = 0;

    public EvaluationFragment() {
        LogUtil.d("xls", "EvaluationFragment  构造方法");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d("xls", "EvaluationFragment  onCreate");
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mEvaluationPresenter = new EvaluationPresenterImpl(getContext(), this);
        evaluationBeans = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d("xls", "EvaluationFragment  onCreateView");
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
                LogUtil.d(TAG, "mLastFeedbackId = " + mLastFeedbackId);
                mEvaluationPresenter.loadEvaluations(mLastFeedbackId, true);
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void bindDataToView(List<EvaluationBean> list) {
        evaluationBeans.clear();
        evaluationBeans.addAll(list);
        mAdapter.setData(filter(evaluationBeans,parameter));
    }


    @Override
    public void appendListData(List<EvaluationBean> list) {
        evaluationBeans.addAll(list);
        mAdapter.setData(filter(evaluationBeans,parameter));
    }

    @Override
    public void saveLastFeedbackId() {
        if (evaluationBeans.size() > 0) {
            Collections.sort(evaluationBeans, new Comparator<EvaluationBean>() {
                @Override
                public int compare(EvaluationBean o1, EvaluationBean o2) {
                    return o1.getId() - o2.getId();
                }
            });
            mLastFeedbackId = evaluationBeans.get(evaluationBeans.size() - 1).getId();
        } else {
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
        if (mHandler != null) {
            mHandler = null;
        }
    }


    @Override
    public void onVisible() {
        super.onVisible();
        LogUtil.d("xls", "EvaluationFragment  Visible ,isResumed = " + isResumed() + "isVisible =" + isVisible());
        if (!isResumed()) {
            LogUtil.d("xls", "EvaluationFragment  is unResumed  ,return");
            return;
        }
        mRunnable = new EvaluationTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtil.d("xls", "EvaluationFragment  InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @OnClick({R.id.rb_all, R.id.rb_very_good, R.id.rb_good, R.id.rb_common, R.id.rb_bad, R.id.rb_very_bad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_all:
                parameter = 0;
                break;
            case R.id.rb_very_good:
                parameter = 5;
                break;
            case R.id.rb_good:
                parameter = 4;
                break;
            case R.id.rb_common:
                parameter = 3;
                break;
            case R.id.rb_bad:
                parameter = 2;
                break;
            case R.id.rb_very_bad:
                parameter = 1;
                break;
        }
        mAdapter.setData(filter(evaluationBeans,parameter));
    }

    private List<EvaluationBean> filter(List<EvaluationBean> all,int deliveryParameter){
        List<EvaluationBean> evaluationBeans = new ArrayList<>();
        if(all==null || all.size()==0){
            return evaluationBeans;
        }
        if(deliveryParameter==0){
            return all;
        }

        for(EvaluationBean evaluationBean:all){
            if(evaluationBean.getDeliveryParameter()==deliveryParameter){
                evaluationBeans.add(evaluationBean);
            }
        }

        Collections.sort(evaluationBeans,new EvaluationListSortComparator());

        return evaluationBeans;
    }

    class EvaluationTaskRunnable implements Runnable {
        @Override
        public void run() {
            mEvaluationPresenter.loadEvaluations(1, false);
        }
    }
}
