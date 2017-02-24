package com.lyancafe.coffeeshop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ShopManagerFragment extends BaseFragment {

    private static final String TAG ="ShopManagerFragment";
    @BindView(R.id.ll_print_paster) LinearLayout printPasterLayout;
    @BindView(R.id.tv_item_paster) TextView printPasterText;
    private Fragment currentFragment;
    private Context mContext;
    private Unbinder unbinder;

    private MaterialFragment materialFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments();

    }
    private void initFragments(){
        materialFragment = new MaterialFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager,container,false);
        unbinder = ButterKnife.bind(this,view);
        initView();
        switchFragment(materialFragment);
        return view;

    }

    private void initView(){
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

    @OnClick(R.id.ll_print_paster)
    void onClick(View v) {
       setSelected(printPasterText,true);
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
        unbinder.unbind();
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
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,fragment).commit();
        fragment.onResume();
        currentFragment = fragment;
    }



    @Override
    protected void onVisible() {
//        Log.d("xls","ShopManagerFragment is onVisible");
    }

    @Override
    protected void onInVisible() {
//        Log.d("xls","ShopManagerFragment is onInVisible");
    }

}
