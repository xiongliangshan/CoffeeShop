package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.Material;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.MaterialItem;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.common.PrintHelper;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;
import com.lyancafe.coffeeshop.shop.presenter.MaterialsPresenter;
import com.lyancafe.coffeeshop.shop.presenter.MaterialsPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.MaiterialsView;
import com.lyancafe.coffeeshop.utils.LogUtil;
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
public class MaterialsFragment extends BaseFragment implements MaiterialsView {

    private static final String TAG = "MaterialsFragment";
    @BindView(R.id.tv_print_paster)
    TextView printPasterText;
    @BindView(R.id.tv_print_material)
    TextView printMaterialText;
    @BindView(R.id.ll_menu_container)
    LinearLayout llMenuContainer;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.contentRV)
    RecyclerView contentRV;


    private MaterialBean toPrintMaterial;
    private Context mContext;

    private Handler mHandler;
    private ManagerTaskRunnable mRunnable;
    private MaterialsPresenter mShopManagerPresenter;


    private View contentView;
    private View[] menuViews;
    private LayoutInflater linflater;
    private String[] listCategorys;
    private TextView[] listMenuTextViews;
    private Bundle savedState;
    private ContentListAdapter mAdapter;
    private List<Material> materials = new ArrayList<>();


    /**
     * 默认的ViewPager选中的项
     */
    private int currentItem = 0;

    public MaterialsFragment() {
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
        mShopManagerPresenter = new MaterialsPresenterImpl(getContext(), this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_materials, container, false);
        ButterKnife.bind(this, contentView);
        linflater = LayoutInflater.from(getContext());
        initRV();
        EventBus.getDefault().register(this);
        return contentView;

    }



    private void initRV() {
        contentRV.setLayoutManager(new GridLayoutManager(mContext, 3));
        contentRV.setHasFixedSize(true);
        contentRV.setItemAnimator(new DefaultItemAnimator());
        ArrayList<MaterialItem> itemList = new ArrayList<>();
        mAdapter= new ContentListAdapter(itemList,getContext());
        contentRV.setAdapter(mAdapter);
    }


    /**
     * 初始化左边目录
     */
    private void initCategorys(View layoutView, List<Material> materials) {
        if(layoutView==null || materials.size()<=0){
            return;
        }
        LogUtil.d(LogUtil.TAG_SHOP,"materials size = "+materials.size());
        listCategorys = new String[materials.size()];
        for (int i = 0; i < materials.size(); i++) {
            listCategorys[i] = materials.get(i).getCategory();
        }
        menuViews = new View[listCategorys.length];
        listMenuTextViews = new TextView[listCategorys.length];
        llMenuContainer.removeAllViews();
        for (int i = 0; i < listCategorys.length; i++) {
            View view = linflater.inflate(R.layout.material_category_menu_item, null);
            // 给每个View设定唯一标识
            view.setId(i);
            // 给每个view添加点击监控事件
            view.setOnClickListener(ListItemMenusClickListener);
            // 获取到左侧栏的的TextView的组件
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(listCategorys[i]);
            llMenuContainer.addView(view);
            // 传入的是地址不是复制的值
            listMenuTextViews[i] = textView;
            menuViews[i] = view;
        }
        changeTextColor(currentItem);
        llMenuContainer.invalidate();
    }



    private void changeTextColor(int position) {
        if(listCategorys==null){
            return;
        }
        for (int i = 0; i < listCategorys.length; i++) {
            if (position != i) {
                listMenuTextViews[i].setBackgroundColor(0x00000000);
                listMenuTextViews[i].setTextColor(0xFF000000);
            }
        }
        listMenuTextViews[position].setBackgroundColor(0xFFFFFFFF);
        listMenuTextViews[position].setTextColor(0xFFFF5D5E);
    }

    private void changeTextLocation(int clickPosition) {
        int y = (menuViews[clickPosition].getTop());
        // 如果滑动条可以滑动的情况下就把点击的视图移动到顶部
        scrollView.smoothScrollTo(0, y);

    }

    View.OnClickListener ListItemMenusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentItem = v.getId();
            mAdapter.setList(materials.get(currentItem).getItems());
            changeTextColor(currentItem);
            changeTextLocation(currentItem);
        }
    };


    @Override
    public void bindDataToListView(List<Material> list) {
        this.materials = list;
        initCategorys(contentView,materials);
        mAdapter.setList(materials.get(currentItem).getItems());
    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.showToast(getActivity(), promptStr);
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


    @OnClick({R.id.tv_print_paster, R.id.tv_print_material, R.id.btn_print_test})
    void onClickPrint(View v) {
        switch (v.getId()) {
            case R.id.tv_print_paster:
                //打印小纸
                MaterialItem itemSmall = mAdapter.getSelectedItem();
                if(itemSmall==null){
                    ToastUtil.showToast(getContext(),getString(R.string.select_material));
                    return;
                }
                PrintHelper.getInstance().printPasterSmall(itemSmall);
                break;
            case R.id.tv_print_material:
                //打印大纸
                MaterialItem itemBig = mAdapter.getSelectedItem();
                if(itemBig==null){
                    ToastUtil.showToast(getContext(),getString(R.string.select_material));
                    return;
                }
                PrintHelper.getInstance().printMaterialBig(itemBig);

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
                ItemContentBean itemContentBean = new ItemContentBean("热拿铁", "大杯", 3000, 1, 3000, 3, null);
                List<ItemContentBean> list = new ArrayList<>();
                list.add(itemContentBean);
                order.setItems(list);
                PrintHelper.getInstance().printOrderInfo(order);
                PrintHelper.getInstance().printOrderItems(order);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreState();
        changeTextColor(currentItem);
        if(materials!=null && materials.size()>0){
            mAdapter.setList(materials.get(currentItem).getItems());
        }
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
        savedState = saveState();
    }

    private void restoreState() {
        if (savedState != null) {
            currentItem = savedState.getInt("index");
        }
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putInt("index", currentItem);
        return state;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onVisible() {
        Log.d("xls", "MaterialsFragment Visible");
        if (!isResumed()) {
            return;
        }
        mRunnable = new ManagerTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);
    }


    @Override
    public void onInVisible() {
        Log.d("xls", "MaterialsFragment InVisible");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }


    private class ManagerTaskRunnable implements Runnable {
        @Override
        public void run() {
            mShopManagerPresenter.loadMaterials();
        }
    }
}
