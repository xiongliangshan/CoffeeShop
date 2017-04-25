package com.lyancafe.coffeeshop.shop.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainShopFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{

    public static int tabIndex = 0;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.vp_container) ViewPager viewPager;
    private Unbinder unbinder;

    private ShopFragmentPagerAdapter mPagerAdapter;

    private FinishedOrderFragment finishedOrderFragment;
//    private TimeEffectFragment timeEffectFragment;
    private EvaluationFragment evaluationFragment;
    private ShopManagerFragment shopManagerFragment;

    public MainShopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_shop, container, false);
        unbinder = ButterKnife.bind(this,contentView);
        initViews();
        return contentView;
    }

    private void initViews() {
        List<Fragment> fragments = new ArrayList<>();
        finishedOrderFragment = new FinishedOrderFragment();
//        timeEffectFragment = new TimeEffectFragment();
        evaluationFragment = new EvaluationFragment();
        shopManagerFragment = new ShopManagerFragment();
        fragments.add(finishedOrderFragment);
//        fragments.add(timeEffectFragment);
        fragments.add(evaluationFragment);
        fragments.add(shopManagerFragment);
        mPagerAdapter = new ShopFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(finishedOrderFragment!=null){
            finishedOrderFragment.onVisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabIndex = tab.getPosition();
        viewPager.setCurrentItem(tabIndex);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
