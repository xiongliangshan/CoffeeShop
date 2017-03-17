package com.lyancafe.coffeeshop.shop.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenter;
import com.lyancafe.coffeeshop.shop.presenter.EvaluationPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.EvaluationView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class EvaluationFragment extends BaseFragment implements EvaluationView {

    private static String TAG = EvaluationFragment.class.getName();
    @BindView(R.id.rb_all) RadioButton rbAll;
    @BindView(R.id.rb_bad_evaluation) RadioButton rbBadEvaluation;
    @BindView(R.id.rb_good_evaluation) RadioButton rbGoodEvaluation;
    @BindView(R.id.rg_select_condition) RadioGroup rgSelectCondition;
    @BindView(R.id.pmrv_evaluation_list) PullLoadMoreRecyclerView pmrvEvaluationList;

    private EvaluationListAdapter mAdapter;
    private int mLastOrderId = 0;
    private int mType = 0;

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
                Log.d(TAG,"mLastOrderId = "+mLastOrderId);
                mEvaluationPresenter.loadEvaluations(mLastOrderId,mType);
            }
        });
    }

    @Override
    public void bindDataToListView(List<EvaluationBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Override
    public void appendListData(List<EvaluationBean> list) {
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
        pmrvEvaluationList.setPullLoadMoreCompleted();
    }

    @Override
    public void bindEvaluationAmount(int positive, int negative) {
        if(rbBadEvaluation!=null && rbGoodEvaluation!=null){
            if(positive>0){
                rbGoodEvaluation.setText("好评("+positive+")");
            }
            if(negative>0){
                rbBadEvaluation.setText("差评("+negative+")");
            }

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

    @OnClick({R.id.rb_all, R.id.rb_bad_evaluation, R.id.rb_good_evaluation})
    public void onClick(View view) {
        rgSelectCondition.check(view.getId());
        OkGo.getInstance().cancelTag("evaluation");
        switch (view.getId()) {
            case R.id.rb_all:
                mType = 0;
                break;
            case R.id.rb_bad_evaluation:
                mType = 5;
                break;
            case R.id.rb_good_evaluation:
                mType = 4;
                break;
        }
        mEvaluationPresenter.loadEvaluations(0,mType);
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
            mEvaluationPresenter.loadEvaluationAmount();
            mEvaluationPresenter.loadEvaluations(0,mType);
        }
    }
}
