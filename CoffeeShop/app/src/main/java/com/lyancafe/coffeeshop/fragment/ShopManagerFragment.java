package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ShopManagerFragment extends Fragment implements View.OnClickListener{

    private static final String TAG ="ShopManagerFragment";
    private View mContentView;
    private LinearLayout printPasterLayout;
    private TextView printPasterText;
    private Fragment currentFragment;
    private FragmentActivity mContext;

    private PrintFragment printFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (FragmentActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments();

    }
    private void initFragments(){
        printFragment = new PrintFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_shop_manager,container,false);
        initView(mContentView);
        switchFragment(printFragment);
        return mContentView;

    }

    private void initView(View contentView){
        printPasterLayout = (LinearLayout) contentView.findViewById(R.id.ll_print_paster);
        printPasterLayout.setOnClickListener(this);
        printPasterText = (TextView) contentView.findViewById(R.id.tv_item_paster);

        setSelected(printPasterText,true);
    }

    //设置左侧菜单选中标志
    private void setSelected(TextView textView,boolean isSelected){
        if(isSelected){
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_menu_selected));
            textView.setTextColor(mContext.getResources().getColor(R.color.font_menu_selected));
        }else{
            textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            textView.setTextColor(mContext.getResources().getColor(R.color.font_munu_normal));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_print_paster:
                setSelected(printPasterText,true);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume--调用:" + FragmentTabAdapter.currentTab);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void switchFragment(Fragment fragment){
        if(fragment == currentFragment || fragment==null){
            return;
        }
        FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,fragment).commit();
        fragment.onResume();
        currentFragment = fragment;
    }



}
