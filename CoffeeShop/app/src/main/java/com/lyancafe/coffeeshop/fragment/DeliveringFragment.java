package com.lyancafe.coffeeshop.fragment;


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
import com.lyancafe.coffeeshop.adapter.DeliveringRvAdapter;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.UpdateDeliverFragmentTabOrderCount;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveringFragment extends BaseFragment implements DeliverFragment.FilterOrdersListenter{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public List<OrderBean> allOrderList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.rv_delivering)
    RecyclerView mRecyclerView;
    private DeliveringRvAdapter mAdapter;
    private Unbinder unbinder;

    private Handler mHandler;
    private DeliveringTaskRunnable mRunnable;

    public DeliveringFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveringFragment newInstance(String param1, String param2) {
        DeliveringFragment fragment = new DeliveringFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("xls","DeliveringFragment-onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mHandler = new Handler();
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
    public void filter(String category) {
        mAdapter.setData(allOrderList,DeliverFragment.category);
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

    //处理服务器返回数据---配送中
    private void handleDeliveryingResponse(XlsResponse xlsResponse,Call call,Response response){
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
        EventBus.getDefault().post(new UpdateDeliverFragmentTabOrderCount(1,orderBeans.size()));
        allOrderList.clear();
        allOrderList.addAll(orderBeans);
        mAdapter.setData(orderBeans,DeliverFragment.category);
    }


    @Override
    protected void onVisible() {
        Log.d("xls","Delivering onVisible");
        if(!isResumed()){
            return;
        }
        mRunnable = new DeliveringTaskRunnable();
        mHandler.postDelayed(mRunnable,OrderHelper.DELAY_LOAD_TIME);

    }

    @Override
    protected void onInVisible() {
        Log.d("xls","Delivering onInVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    class DeliveringTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqDeliveryingData(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleDeliveryingResponse(xlsResponse,call,response);
                }

            });
        }
    }
}
