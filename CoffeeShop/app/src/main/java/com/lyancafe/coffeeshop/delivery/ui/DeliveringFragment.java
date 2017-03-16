package com.lyancafe.coffeeshop.delivery.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.delivery.presenter.DeliveringPresenter;
import com.lyancafe.coffeeshop.delivery.presenter.DeliveringPresenterImpl;
import com.lyancafe.coffeeshop.delivery.view.DeliveringView;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveringFragment extends BaseFragment implements MainDeliverFragment.FilterOrdersListenter,DeliveringView{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public List<OrderBean> allOrderList = new ArrayList<>();

    @BindView(R.id.rv_delivering)
    RecyclerView mRecyclerView;
    private DeliveringRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private DeliveringTaskRunnable mRunnable;

    private DeliveringPresenter mDeliveringPresenter;

    public DeliveringFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mDeliveringPresenter  =  new DeliveringPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_delivering, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        mAdapter = new DeliveringRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void bindDataToListView(List<OrderBean> list) {
        allOrderList.clear();
        allOrderList.addAll(list);
        mAdapter.setData(list, MainDeliverFragment.category);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Override
    public void filter(String category) {
        mAdapter.setData(allOrderList, MainDeliverFragment.category);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","DeliveringFragment-onPause");
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
        Log.d("xls","Delivering onVisible");
        if(!isResumed()){
            return;
        }
        mRunnable = new DeliveringTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    public void onInVisible() {
        Log.d("xls","Delivering onInVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    class DeliveringTaskRunnable implements Runnable{
        @Override
        public void run() {
            mDeliveringPresenter.loadDeliveringOrderList();
        }
    }
}
