package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.common.PrintHelper;
import com.lyancafe.coffeeshop.shop.presenter.ShopManagerPresenter;
import com.lyancafe.coffeeshop.shop.presenter.ShopManagerPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.ShopManagerView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ShopManagerFragment extends BaseFragment implements ShopManagerView{

    private static final String TAG ="ShopManagerFragment";
    @BindView(R.id.rb_normal) RadioButton rbNormal;
    @BindView(R.id.rb_new) RadioButton rbNew;
    @BindView(R.id.rg_printer) RadioGroup rgPrinter;
    @BindView(R.id.tv_print_paster) TextView printPasterText;
    @BindView(R.id.tv_print_material) TextView printMaterialText;
    @BindView(R.id.rv_material) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ContentLoadingProgressBar clpBar;

    private MaterialAdatapter materialAdatapter;
    private MaterialBean toPrintMaterial;
    private Context mContext;
    private Unbinder unbinder;

    private Handler mHandler;
    private ManagerTaskRunnable mRunnable;
    private ShopManagerPresenter mShopManagerPresenter;

    public ShopManagerFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mShopManagerPresenter = new ShopManagerPresenterImpl(getContext(),this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager,container,false);
        unbinder = ButterKnife.bind(this,view);
        initView();
        EventBus.getDefault().register(this);
        return view;

    }

    private void initView(){
        if(PrintHelper.getInstance().getProperty()){
            rgPrinter.check(R.id.rb_new);
        }else{
            rgPrinter.check(R.id.rb_normal);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<MaterialBean> itemList = new ArrayList<>();
        materialAdatapter = new MaterialAdatapter(itemList, mContext);
        recyclerView.setAdapter(materialAdatapter);
    }

    @Override
    public void bindDataToListView(List<MaterialBean> list) {
        materialAdatapter.setData(list);
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(),promptStr);
    }

    @Subscribe
    public void OnMessageEvent(MaterialSelectEvent event) {
        if (event.selected >= 0) {
            toPrintMaterial = event.materialBean;
            printMaterialText.setEnabled(true);
            printMaterialText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_black_circle));
            printMaterialText.setTextColor(mContext.getResources().getColor(R.color.white_font));
        } else {
            toPrintMaterial = null;
            printMaterialText.setEnabled(false);
            printMaterialText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_white_circle));
            printMaterialText.setTextColor(mContext.getResources().getColor(R.color.text_black));
        }
    }


    @OnClick({R.id.tv_print_paster, R.id.tv_print_material,R.id.btn_print_test})
    void onClickPrint(View v) {
        switch (v.getId()) {
            case R.id.tv_print_paster:
                PrintHelper.getInstance().printPasterSmall();
                break;
            case R.id.tv_print_material:
                //开始打印
                if (toPrintMaterial != null) {
                    PrintHelper.getInstance().printMaterialBig(toPrintMaterial);
                }
                break;
            case R.id.btn_print_test:
                //测试打印机
                OrderBean order = new OrderBean();
                order.setId(65535);
                order.setOrderSn("2017032765535");
                order.setShopOrderNo(666);
                order.setOrderVip(false);
                order.setWxScan(false);
                order.setIsRecipeFittings(false);
                order.setGift(1);
                order.setWishes("");
                order.setExpectedTime(0L);
                order.setOrderTime(0L);
                order.setProduceEffect(0L);
                order.setRecipient("连咖啡");
                order.setAddress("徐汇区古美路1515号凤凰大楼1801");
                order.setPhone("18516152485");
                order.setCourierName("张三");
                order.setCourierPhone("13645781245");
                order.setStatus(3010);
                order.setProduceStatus(4005);
                order.setIssueOrder(false);
                order.setInstant(1);
                order.setNotes("多放点糖");
                order.setCsrNotes("");
                order.setPlatform("微信");
                order.setHandoverTime(0L);
                order.setFeedbackTags(new ArrayList<String>());
                order.setFeedback("");
                order.setFeedbackType(0);
                order.setDeliveryTeam(4);
                order.setPlatformId(12);
                order.setMtShopOrderNo(0);
                order.setOrderDistance(1300);
                ItemContentBean itemContentBean = new ItemContentBean("热拿铁","大杯",3000,1,3000,3,null);
                List<ItemContentBean> list = new ArrayList<>();
                list.add(itemContentBean);
                order.setItems(list);
                PrintHelper.getInstance().printOrderInfo(order);
                PrintHelper.getInstance().printOrderItems(order);
                break;
        }
    }


    @OnClick({R.id.rb_normal, R.id.rb_new})
    public void onClickRadioBt(View view) {
        rgPrinter.check(view.getId());
        switch (view.getId()) {
            case R.id.rb_normal:
                PrintHelper.getInstance().saveProperty(false);
                break;
            case R.id.rb_new:
                PrintHelper.getInstance().saveProperty(true);
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
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler=null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onVisible() {
        Log.d("xls","ShopManagerFragment Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ManagerTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);
    }



    @Override
    public void onInVisible() {
        Log.d("xls","ShopManagerFragment InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }


    private class ManagerTaskRunnable implements Runnable{
        @Override
        public void run() {
          mShopManagerPresenter.loadMaterialList();
        }
    }
}
