package com.lyancafe.coffeeshop.shop.ui;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.shop.presenter.StatementPresenter;
import com.lyancafe.coffeeshop.shop.presenter.StatementPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.StatementView;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.widget.PiePercentView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatementFragment extends BaseFragment implements StatementView {

    private static final String TAG = "StatementFragment";

    @BindView(R.id.tv_finished_order_count)
    TextView tvFinishedOrderCount;
    @BindView(R.id.tv_all_order_count)
    TextView tvAllOrderCount;
    @BindView(R.id.tv_finished_cup_count)
    TextView tvFinishedCupCount;
    @BindView(R.id.tv_all_cup_count)
    TextView tvAllCupCount;
    Unbinder unbinder;
    @BindView(R.id.cpv)
    PiePercentView cpv;

    private StatementPresenter mStatementPresenter;
    private final int DURATION = 1000; //ms

    ValueAnimator[] animators = new ValueAnimator[4];

    public StatementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatementPresenter = new StatementPresenterImpl(getContext(), this);
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
        if(isVisible){
            animateShow(allOrdersCount, finishedOrdersCount, allCupsCount, finishedCupsCount);
        }
    }

    @Override
    public void bindPie(List<PiePercentView.PieData> pieDatas) {
        if(pieDatas.size()==0){
            return;
        }
        cpv.setData(pieDatas);
        animatePie();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void animateShow(int allOrdersCount, int finishedOrdersCount, int allCupsCount, int finishedCupsCount) {
        LogUtil.d(TAG,"animateShow "+allCupsCount+"|"+finishedCupsCount+"|"+allCupsCount+"|"+finishedCupsCount);
        animators[0] = ValueAnimator.ofInt(0, allOrdersCount);
        animators[1] = ValueAnimator.ofInt(0, finishedOrdersCount);
        animators[2] = ValueAnimator.ofInt(0, allCupsCount);
        animators[3] = ValueAnimator.ofInt(0, finishedCupsCount);

        animators[0].setDuration(allOrdersCount * 4);
        animators[1].setDuration(finishedOrdersCount * 4);
        animators[2].setDuration(allCupsCount * 4);
        animators[3].setDuration(finishedCupsCount * 4);

        animators[0].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvAllOrderCount.setText(String.valueOf(value));
            }
        });
        animators[1].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvFinishedOrderCount.setText(String.valueOf(value));
            }
        });
        animators[2].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvAllCupCount.setText(String.valueOf(value));
            }
        });
        animators[3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvFinishedCupCount.setText(String.valueOf(value));
            }
        });

        for (ValueAnimator animator : animators) {
            animator.start();
        }

    }

    private void animatePie(){
        if(cpv==null){
            return;
        }
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(cpv,"scaleX",0,1).setDuration(800);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(cpv,"scaleY",0,1).setDuration(800);
        ObjectAnimator objectAnimatorZ = ObjectAnimator.ofFloat(cpv,"rotationY",-540,0).setDuration(800);
        objectAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimatorZ.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY,objectAnimatorZ);
        animatorSet.start();
    }


    @Override
    public void onVisible() {
        super.onVisible();
        if (!isResumed()) {
            LogUtil.d(TAG, "StatementFragment onVisible "+isVisible);
            return;
        }
        LogUtil.d(TAG, "StatementFragment onVisible & Resumed "+isVisible);
        mStatementPresenter.calculateCount();

    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtil.d(TAG, "StatementFragment onInVisible "+isVisible);
        for (ValueAnimator animator : animators) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }
}
