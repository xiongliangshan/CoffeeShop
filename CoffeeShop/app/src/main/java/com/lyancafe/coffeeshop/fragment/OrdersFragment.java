package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.adapter.SubFragmentTabAdapter;
import com.lyancafe.coffeeshop.widget.BadgeView;
import com.lyancafe.coffeeshop.widget.ListRadioButton;
import com.lyancafe.coffeeshop.widget.ListTabButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment {

    private static final String TAG  ="OrdersFragment";
    private View mContentView;
    private Activity mContext;
    public static int subTabIndex = 0;

    private ListTabButton toDoTab;
    private ListTabButton haveDoneTab;
    private ListTabButton deliveringTab;
    private ListTabButton deliveryFinishedTab;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG, "onAttach");
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
        initViews(mContentView,mContext);
        return mContentView;
    }

    private void initViews(View contentView,Context context){
        toDoTab = (ListTabButton) contentView.findViewById(R.id.rb_to_do);
        haveDoneTab = (ListTabButton) contentView.findViewById(R.id.rb_have_done);
        deliveringTab = (ListTabButton) contentView.findViewById(R.id.rb_delivering);
        deliveryFinishedTab = (ListTabButton) contentView.findViewById(R.id.rb_delivery_finished);
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
