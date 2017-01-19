package com.lyancafe.coffeeshop.fragment;


import android.os.Bundle;
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
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.RefreshToFetchDataEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToFetchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToFetchFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private ToFetchRvAdapter mAdapter;


    public ToFetchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToFetchFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        Log.d("xls","ToFetchFragment-onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("xls","ToFetchFragment-onResume");
        HttpHelper.getInstance().reqProducedData(2, 99, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleProudcedResponse(xlsResponse,call,response);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","ToFetchFragment-onPause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("xls","ToFetchFragment-onDetach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View contentView =  inflater.inflate(R.layout.fragment_to_fetch, container, false);
        initViews(contentView);
        return contentView;
    }

    private void initViews(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_to_fetch);
        mAdapter = new ToFetchRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onRefreshToFetchDataEvent(RefreshToFetchDataEvent event){

        //请求待取货列表
        HttpHelper.getInstance().reqProducedData(2, 99, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleProudcedResponse(xlsResponse,call,response);
            }
        });

    }


    //处理服务器返回数据---已生产
    private void handleProudcedResponse(XlsResponse xlsResponse,Call call,Response response){
        List<OrderBean> orderBeans = OrderBean.parseJsonOrders(getActivity(), xlsResponse);
 //       EventBus.getDefault().post(new UpdateTabOrderListCountEvent(TabList.TAB_PRODUCED,orderBeans.size()));
        mAdapter.setData(orderBeans);
    }


}
