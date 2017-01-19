package com.lyancafe.coffeeshop.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.lyancafe.coffeeshop.fragment.DeliverFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */

public class DeliverFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"待取货","配送中"};
    private List<Fragment> fragments;

    public DeliverFragmentPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
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
