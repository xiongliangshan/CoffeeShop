package com.lyancafe.coffeeshop.shop.ui;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.shop.presenter.StatementPresenter;
import com.lyancafe.coffeeshop.shop.presenter.StatementPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.StatementView;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatementFragment extends BaseFragment implements StatementView {


    @BindView(R.id.tv_finished_order_count)
    TextView tvFinishedOrderCount;
    @BindView(R.id.tv_all_order_count)
    TextView tvAllOrderCount;
    @BindView(R.id.tv_finished_cup_count)
    TextView tvFinishedCupCount;
    @BindView(R.id.tv_all_cup_count)
    TextView tvAllCupCount;
    Unbinder unbinder;

    private StatementPresenter mStatementPresenter;
    private final int DURATION = 1000; //ms

    public StatementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatementPresenter = new StatementPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statement, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void bindData(Map<String, Integer> map) {
        int allOrdersCount = map.get("allOrders");
        int finishedOrdersCount = map.get("finishedOrders");
        int allCupsCount = map.get("allCups");
        int finishedCupsCount = map.get("finishedCups");

        /*tvAllOrderCount.setText(String.valueOf(allOrdersCount));
        tvFinishedOrderCount.setText(String.valueOf(finishedOrdersCount));
        tvAllCupCount.setText(String.valueOf(allCupsCount));
        tvFinishedCupCount.setText(String.valueOf(finishedCupsCount));*/
        animateShow(allOrdersCount,finishedOrdersCount,allCupsCount,finishedCupsCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void animateShow(int allOrdersCount,int finishedOrdersCount,int allCupsCount,int finishedCupsCount ){
        ValueAnimator animator1 = ValueAnimator.ofInt(0,allOrdersCount);
        ValueAnimator animator2 = ValueAnimator.ofInt(0,finishedOrdersCount);
        ValueAnimator animator3 = ValueAnimator.ofInt(0,allCupsCount);
        ValueAnimator animator4 = ValueAnimator.ofInt(0,finishedCupsCount);

        animator1.setDuration(allOrdersCount*4);
        animator2.setDuration(finishedOrdersCount*4);
        animator3.setDuration(allCupsCount*4);
        animator4.setDuration(finishedCupsCount*4);

        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvAllOrderCount.setText(String.valueOf(value));
            }
        });
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvFinishedOrderCount.setText(String.valueOf(value));
            }
        });
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvAllCupCount.setText(String.valueOf(value));
            }
        });
        animator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvFinishedCupCount.setText(String.valueOf(value));
            }
        });

        animator1.start();
        animator2.start();
        animator3.start();
        animator4.start();

    }



    @Override
    public void onVisible() {
        super.onVisible();
        LogUtil.d("xls","StatementFragment onVisible");
        if(!isResumed()){
            return;
        }
        mStatementPresenter.calculateCount();

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtil.d("xls","StatementFragment onInVisible");
    }
}
