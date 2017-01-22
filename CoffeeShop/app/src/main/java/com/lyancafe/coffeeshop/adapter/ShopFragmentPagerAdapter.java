package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */

public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

    public String[] titles = new String[]{"订单一览","门店管理"};
    private List<Fragment> fragments;
    private Context mContext;

    public ShopFragmentPagerAdapter(FragmentManager fm, Context mContext, List<Fragment> fragments) {
        super(fm);
        this.mContext = mContext;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }



}
