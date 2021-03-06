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
import com.lyancafe.coffeeshop.logger.Logger;

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


//    private EvaluationFragment evaluationFragment;
    private MaterialsFragment materialFragment;
    private ManagerFragment managerFragment;
    private StatementFragment statementFragment;
    private TrainingVideoFragment trainingVideoFragment;

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
//        evaluationFragment = new EvaluationFragment();
        materialFragment = new MaterialsFragment();
        managerFragment = new ManagerFragment();
        statementFragment = new StatementFragment();
        trainingVideoFragment = new TrainingVideoFragment();
//        fragments.add(evaluationFragment);
        fragments.add(materialFragment);
        fragments.add(managerFragment);
        fragments.add(statementFragment);
        fragments.add(trainingVideoFragment);
        mPagerAdapter = new ShopFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(evaluationFragment!=null){
            evaluationFragment.onVisible();
        }*/
        if(materialFragment!=null){
            managerFragment.onVisible();
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
        Logger.getLogger().log("切换到 门店--"+tabIndex);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
