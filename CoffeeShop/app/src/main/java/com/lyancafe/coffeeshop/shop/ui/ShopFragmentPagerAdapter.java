package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lyancafe.coffeeshop.logger.Logger;

import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */

public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

    public String[] titles = new String[]{"评价列表","打印物料","门店管理","门店报表","培训视频"};
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
