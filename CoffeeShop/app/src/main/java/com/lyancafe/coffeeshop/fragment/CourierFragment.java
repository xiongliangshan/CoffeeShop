package com.lyancafe.coffeeshop.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.CourierRvAdapter;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.DeliverBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CourierFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.rv_courier_list)
    RecyclerView rvCourierList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Unbinder unbinder;
    private CourierRvAdapter mAdapter;

    public CourierFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static CourierFragment newInstance(String param1, String param2) {
        CourierFragment fragment = new CourierFragment();
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
        View view = inflater.inflate(R.layout.fragment_courier, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView(){
        rvCourierList.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvCourierList.setHasFixedSize(true);
        rvCourierList.addItemDecoration(new SpaceItemDecoration(2, OrderHelper.dip2Px(16, getActivity()), false));
        mAdapter = new CourierRvAdapter(createTestData());
        rvCourierList.setAdapter(mAdapter);
    }

    private List<DeliverBean> createTestData() {
        List<DeliverBean> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            DeliverBean d = new DeliverBean();
            d.setId(i);
            d.setName("熊"+i);
            d.setPhone("13515454555");
            d.setDistanceToShop(10*i);
            d.setTotalOrderCount(i*5);
            d.setDeliveringOrderCount(i+3);
            list.add(d);
        }
        return list;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
