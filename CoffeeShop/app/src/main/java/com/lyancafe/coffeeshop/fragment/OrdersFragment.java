package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.adapter.SubFragmentTabAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment {

    private static final String TAG  ="OrdersFragment";
    private View mContentView;
    private FragmentActivity mActivity;
    private static RadioGroup mGroup;
    public static int subTabIndex = 0;
    public List<Fragment> fragments_list = new ArrayList<Fragment>();
    private SubFragmentTabAdapter mTabAdapter;

    private SubToMakeFragment toMakeFragment;
    private SubHaveMakedFragment haveMakedFragment;
    private SubDeliveringFragment deliveringFragment;
    private SubFinishedFragment finishedFragment;


    public OrdersFragment() {
        Log.d(TAG,"OrdersFragment()构造方法");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    public OrdersFragment(FragmentActivity activity) {
        this.mActivity = activity;
        Log.d(TAG, "OrdersFragment()带参构造方法");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(mContentView);
        initFragments();
        return mContentView;
    }

    private void initViews(View contentView){
        mGroup  = (RadioGroup) contentView.findViewById(R.id.radio_group);
    }

    private void initFragments(){
        toMakeFragment =  new SubToMakeFragment();
        haveMakedFragment = new SubHaveMakedFragment();
        deliveringFragment = new SubDeliveringFragment();
        finishedFragment = new SubFinishedFragment();

        fragments_list.add(toMakeFragment);
        fragments_list.add(haveMakedFragment);
        fragments_list.add(deliveringFragment);
        fragments_list.add(finishedFragment);



        mTabAdapter = new SubFragmentTabAdapter(mActivity, fragments_list, R.id.fragment_container, mGroup);
        mTabAdapter.setOnRgsExtraCheckedChangedListener(new SubFragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                subTabIndex = index;
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

}
