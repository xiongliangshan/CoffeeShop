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
import com.lyancafe.coffeeshop.utils.LogUtil;

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

    public boolean isScrolled = false;

    private Unbinder unbinder;

    private ShopFragmentPagerAdapter mPagerAdapter;

    private FinishedOrderFragment finishedOrderFragment;
//    private TimeEffectFragment timeEffectFragment;
    private EvaluationFragment evaluationFragment;
    private MaterialsFragment materialFragment;
    private ManagerFragment managerFragment;

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
        evaluationFragment = new EvaluationFragment();
        materialFragment = new MaterialsFragment();
        managerFragment = new ManagerFragment();
        fragments.add(finishedOrderFragment);
        fragments.add(evaluationFragment);
        fragments.add(materialFragment);
        fragments.add(managerFragment);
        mPagerAdapter = new ShopFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtil.d(LogUtil.TAG_SHOP,"viewpager is scrolling :"+isScrolled+" | "+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case ViewPager.SCROLL_STATE_IDLE:
                        isScrolled = false;
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isScrolled = true;
                        break;
                }
                LogUtil.d(LogUtil.TAG_SHOP,"onPageScrollStateChanged :"+state);
            }
        });

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
