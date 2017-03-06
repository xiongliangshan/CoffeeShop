package com.lyancafe.coffeeshop.fragment;

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
import com.lyancafe.coffeeshop.adapter.EvaluationListAdapter;
import com.lyancafe.coffeeshop.bean.EvaluationBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
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

public class EvaluationFragment extends BaseFragment {

    private static String TAG = EvaluationFragment.class.getName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.rb_all) RadioButton rbAll;
    @BindView(R.id.rb_bad_evaluation) RadioButton rbBadEvaluation;
    @BindView(R.id.rb_good_evaluation) RadioButton rbGoodEvaluation;
    @BindView(R.id.rg_select_condition) RadioGroup rgSelectCondition;
    @BindView(R.id.pmrv_evaluation_list) PullLoadMoreRecyclerView pmrvEvaluationList;

    private EvaluationListAdapter mAdapter;
    private int mLastOrderId = 0;
    private int mType = 0;

    private String mParam1;
    private String mParam2;

    private Unbinder unbinder;

    private Handler mHandler;
    private EvaluationTaskRunnable mRunnable;

    public EvaluationFragment() {

    }


    public static EvaluationFragment newInstance(String param1, String param2) {
        EvaluationFragment fragment = new EvaluationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mHandler = new Handler();
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
                HttpHelper.getInstance().reqEvaluationListData(mLastOrderId,mType, new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleEvaluationListDataResponse(xlsResponse,call,response);
                        pmrvEvaluationList.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        pmrvEvaluationList.setPullLoadMoreCompleted();
                    }
                });


            }
        });
    }


    /**
     * 处理评论数量接口返回的数据
     */
    private void handleCommentCountResponse(XlsResponse xlsResponse,Call call,Response response){
        if(xlsResponse.status==0){
            int positive = xlsResponse.data.getIntValue("positive");
            int negative = xlsResponse.data.getIntValue("negative");
            Log.d(TAG,"positive = "+positive+",negative = "+negative);
            if(rbBadEvaluation!=null && rbGoodEvaluation!=null){
                if(positive>0){
                    rbGoodEvaluation.setText("好评("+positive+")");
                }
                if(negative>0){
                    rbBadEvaluation.setText("差评("+negative+")");
                }

            }

        }
    }

    private void handleEvaluationListDataResponse(XlsResponse xlsResponse, Call call, Response response) {
        Log.d(TAG,"xlsponse = "+xlsResponse);
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<EvaluationBean> list = EvaluationBean.parseJsonOrders(getContext(),xlsResponse);
        if("yes".equalsIgnoreCase(isLoadMore)){
            mAdapter.addData(list);
        }else{
            mAdapter.setData(list);
        }

        if(mAdapter.list.size()>0){
            mLastOrderId = mAdapter.list.get(mAdapter.list.size()-1).getOrderId();
        }else {
            mLastOrderId = 0;
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
        HttpHelper.getInstance().reqEvaluationListData(0, mType, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleEvaluationListDataResponse(xlsResponse,call,response);
            }
        });
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        Log.d("xls","EvaluationFragment  Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new EvaluationTaskRunnable();
        mHandler.postDelayed(mRunnable,1000);

    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        Log.d("xls","EvaluationFragment  InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    class EvaluationTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqCommentCount(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleCommentCountResponse(xlsResponse,call,response);
                }
            });
            HttpHelper.getInstance().reqEvaluationListData(0, mType, new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleEvaluationListDataResponse(xlsResponse,call,response);
                }
            });
        }
    }
}
