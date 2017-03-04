package com.lyancafe.coffeeshop.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.TimeEffectListAdapter;
import com.lyancafe.coffeeshop.bean.TimeEffectBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    private Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TimeEffectListAdapter mAdapter;


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

        mAdapter.setData(testData());
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        rvEffectListView.setPullLoadMoreCompleted();
    }

    public List<TimeEffectBean> testData(){
        List<TimeEffectBean> list = new ArrayList<>();
        for(int i=0;i<20;i++){
            TimeEffectBean t = new TimeEffectBean();
            t.setShopOrderNo(1);
            t.setOrderId(665348);
            t.setInstant(0);
            t.setOrderTime(164515156L);
            t.setExceptedTime(4564454L);
            t.setProducedTime(452345645L);
            t.setGrabTime(84968556L);
            t.setFetchTime(225665654L);
            t.setDeliveredTime(565444454L);
            t.setDeliverName("熊良山");
            list.add(t);
        }

        return list;
    }

    @OnClick({R.id.rb_all, R.id.rb_bad, R.id.rb_normal, R.id.rb_great})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_all:
                break;
            case R.id.rb_bad:
                break;
            case R.id.rb_normal:
                break;
            case R.id.rb_great:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        Log.d("xls","TimeEffectFragment  Visible");
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        Log.d("xls","TimeEffectFragment  InVisible");
    }
}
