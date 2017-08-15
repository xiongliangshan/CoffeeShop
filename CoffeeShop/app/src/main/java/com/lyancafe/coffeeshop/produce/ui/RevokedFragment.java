package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.event.ChangeTabCountByActionEvent;
import com.lyancafe.coffeeshop.event.RevokeEvent;
import com.lyancafe.coffeeshop.produce.presenter.RevokedPresenter;
import com.lyancafe.coffeeshop.produce.presenter.RevokedPresenterImpl;
import com.lyancafe.coffeeshop.produce.view.RevokedView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/11.
 */

public class RevokedFragment extends BaseFragment implements RevokedView {


    @BindView(R.id.rv_revoked)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private RevokedRvAdapter mAdapter;
    private RevokedPresenter mRevokedPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRevokedPresenter = new RevokedPresenterImpl(getContext(),this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_revoked, container, false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        EventBus.getDefault().register(this);
        return contentView;
    }


    private void initViews() {
        mAdapter = new RevokedRvAdapter(getContext());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, OrderHelper.dip2Px(12, getActivity()), false));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void bindDataToView(List<OrderBean> list) {
        if(list==null){
            return;
        }
        mAdapter.setData(list);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.show(getContext(),promptStr);
    }

    //订单撤销事件
    @Subscribe
    public void onRevokeEvent(RevokeEvent event) {
        mRevokedPresenter.loadRevokedOrders();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        mRevokedPresenter.loadRevokedOrders();

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
    }
}
