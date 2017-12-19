package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lyancafe.coffeeshop.logger.Logger;

import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */

public class ProduceFragmentPagerAdapter extends FragmentPagerAdapter {

    public String[] titles = new String[]{"待生产","生产中","已生产","已完成","被撤回","明日预定"};
    private List<Fragment> fragments;
    private Context mContext;

    public ProduceFragmentPagerAdapter(FragmentManager fm, Context mContext, List<Fragment> fragments) {
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
