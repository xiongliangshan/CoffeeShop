package com.lyancafe.coffeeshop.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.DeliverFragmentPagerAdapter;
import com.lyancafe.coffeeshop.adapter.ShopFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{

    public static int tabIndex = 0;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ShopFragmentPagerAdapter mPagerAdapter;

    public ShopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_shop, container, false);
        initViews(contentView);
        return contentView;
    }

    private void initViews(View contentView) {
        tabLayout  = (TabLayout) contentView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_container);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FinishedOrderFragment());
        fragments.add(new ShopManagerFragment());
        mPagerAdapter = new ShopFragmentPagerAdapter(getChildFragmentManager(),getActivity(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

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


    @Override
    protected void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        for(int i=0;i<mPagerAdapter.getCount();i++){
            if(i==tabIndex){
                mPagerAdapter.getItem(i).setUserVisibleHint(true);
            }else{
                mPagerAdapter.getItem(i).setUserVisibleHint(false);
            }
        }
    }


}
