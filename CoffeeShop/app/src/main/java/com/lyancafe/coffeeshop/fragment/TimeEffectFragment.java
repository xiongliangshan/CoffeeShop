package com.lyancafe.coffeeshop.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.TimeEffectListAdapter;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeEffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeEffectFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TimeEffectListAdapter mAdapter;

    private Handler mHandler;
    private TimeEffectTaskRunnable mRunnable;


    public TimeEffectFragment() {
        // Required empty public constructor
    }


    public static TimeEffectFragment newInstance(String param1, String param2) {
        TimeEffectFragment fragment = new TimeEffectFragment();
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
    public void onLoadMore() {
        HttpHelper.getInstance().reqTimeEffectList(mLastOrderId, mType, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                rvEffectListView.setPullLoadMoreCompleted();
                handleTimeEffectListResponse(xlsResponse,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                rvEffectListView.setPullLoadMoreCompleted();
            }
        });

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
        HttpHelper.getInstance().reqTimeEffectList(0, mType, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleTimeEffectListResponse(xlsResponse,call,response);
            }
        });
    }


    class TimeEffectTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqTimeEffectTypeCount(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleTimeEffectCountResponse(xlsResponse,call,response);
                }
            });
            HttpHelper.getInstance().reqTimeEffectList(0, mType, new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleTimeEffectListResponse(xlsResponse,call,response);
                }
            });
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

    private void handleTimeEffectCountResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            int totalCount = xlsResponse.data.getIntValue("totalCount");
            int goodCount =xlsResponse.data.getIntValue("goodCount");
            int passedCount = xlsResponse.data.getIntValue("passedCount");
            int fallingCount = xlsResponse.data.getIntValue("fallingCount");
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
    }

    private void handleTimeEffectListResponse(XlsResponse xlsResponse, Call call, Response response) {
        Request request = call.request();
        String isLoadMore = request.header("isLoadMore");
        List<TimeEffectBean> timeEffectBeanList = TimeEffectBean.parseJsonOrders(getContext(),xlsResponse);
        Log.d("xls","list.size = "+timeEffectBeanList.size());
        if("yes".equalsIgnoreCase(isLoadMore)){
            mAdapter.addData(timeEffectBeanList);
        }else{
            mAdapter.setData(timeEffectBeanList);
        }
        if(mAdapter.list.size()>0){
            mLastOrderId = mAdapter.list.get(mAdapter.list.size()-1).getOrderId();
        }else {
            mLastOrderId = 0;
        }
    }


}
