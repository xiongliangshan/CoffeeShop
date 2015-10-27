package com.lyancafe.coffeeshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.igexin.sdk.PushManager;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.fragment.OrderQueryFragment;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.fragment.ShopManagerFragment;
import com.lyancafe.coffeeshop.widget.SettingWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeActivity extends BaseActivity {

    public static int currIndex = 0;
    private static RadioGroup mRadioGroup;
    public List<Fragment> fragments_list = new ArrayList<Fragment>();
    private OrdersFragment orderFrag;
    private OrderQueryFragment orderQueryFrag;
    private ShopManagerFragment shopManagerFrag;
    private FragmentTabAdapter tabAdapter;
    private ImageButton settingBtn;
    private SettingWindow sw;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化个推
        PushManager.getInstance().initialize(this.getApplicationContext());

        setContentView(R.layout.activity_home);
        initViews();
        initFragments();
    }

    private void initViews(){
        mRadioGroup = (RadioGroup) findViewById(R.id.group_left);
        settingBtn = (ImageButton) findViewById(R.id.btn_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //左下角的设置
                if(sw!=null){
                    sw.showSettingWindow(v);
                }else{
                    SettingWindow sw  = new SettingWindow(HomeActivity.this);
                    sw.showSettingWindow(v);
                }

            }
        });
    }
    private void initFragments(){
        orderFrag =  new OrdersFragment();
        orderQueryFrag = new OrderQueryFragment();
        shopManagerFrag = new ShopManagerFragment();
        fragments_list.add(orderFrag);
        fragments_list.add(orderQueryFrag);
        fragments_list.add(shopManagerFrag);


        tabAdapter = new FragmentTabAdapter(HomeActivity.this, fragments_list, R.id.tab_content, mRadioGroup);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                currIndex = index;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
