package com.lyancafe.coffeeshop.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.ToFetchRvAdapter;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.event.RecallOrderEvent;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.event.UpdateOrderDetailEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToFetchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToFetchFragment extends BaseFragment implements DeliverFragment.FilterOrdersListenter{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public List<OrderBean> allOrderList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.rv_to_fetch) RecyclerView mRecyclerView;
    private ToFetchRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private ToFetchTaskRunnable mRunnale;

    public ToFetchFragment() {
        // Required empty public constructor
    }


    public static ToFetchFragment newInstance(String param1, String param2) {
        ToFetchFragment fragment = new ToFetchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("xls","ToFetchFragment-onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mHandler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("xls","ToFetchFragment-onCreateView");
        EventBus.getDefault().register(this);
        View contentView =  inflater.inflate(R.layout.fragment_to_fetch, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews(contentView);
        return contentView;
    }

    private void initViews(View contentView) {
        mAdapter = new ToFetchRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(4, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HttpHelper.getInstance().reqProducedData(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleProudcedResponse(xlsResponse,call,response);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("xls","ToFetchFragment-onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","ToFetchFragment-onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("xls","ToFetchFragment-onDetach");
    }

    @Override
    public void filter(String category) {
        mAdapter.setData(allOrderList,DeliverFragment.category);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 咖啡师从小哥手里撤回订单
     * @param event
     */
    @Subscribe
    public  void onRecallOrderEvent(RecallOrderEvent event){
        if(event.tabIndex==10){
            for(int i=0;i<mAdapter.list.size();i++) {
                OrderBean order = mAdapter.list.get(i);
                if (event.orderId == order.getId()) {
                    order.setStatus(OrderStatus.UNASSIGNED);
                    mAdapter.notifyItemChanged(i);
                    EventBus.getDefault().post(new UpdateOrderDetailEvent(order));
                    break;
                }
            }
        }
    }



    //处理服务器返回数据---已生产
    private void handleProudcedResponse(XlsResponse xlsResponse,Call call,Response response){
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
        EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(0,orderBeans.size()));
        allOrderList.clear();
        allOrderList.addAll(orderBeans);
        mAdapter.setData(orderBeans,DeliverFragment.category);
    }





    @Override
    public void onVisible() {
        Log.d("xls","ToFetchFragment Visible");
        if(!isResumed()){
            return;
        }
        mRunnale = new ToFetchTaskRunnable();
        mHandler.postDelayed(mRunnale,OrderHelper.DELAY_LOAD_TIME);
    }

    @Override
    public void onInVisible() {
        Log.d("xls","ToFetchFragment InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnale);
        }
    }


    class ToFetchTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqProducedData(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleProudcedResponse(xlsResponse,call,response);
                }
            });
        }
    }
}
