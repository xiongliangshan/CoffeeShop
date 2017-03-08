package com.lyancafe.coffeeshop.fragment;

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
import com.lyancafe.coffeeshop.adapter.FragmentTabAdapter;
import com.lyancafe.coffeeshop.adapter.MaterialAdatapter;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ShopManagerFragment extends BaseFragment {

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


    @OnClick({R.id.tv_print_paster, R.id.tv_print_material})
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
    protected void onVisible() {
        Log.d("xls","ShopManagerFragment Visible");
        if(!isResumed()){
            return;
        }
        mRunnable = new ManagerTaskRunnable();
        mHandler.postDelayed(mRunnable, OrderHelper.DELAY_LOAD_TIME);
    }



    @Override
    protected void onInVisible() {
        Log.d("xls","ShopManagerFragment InVisible");
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }


    private void handleMaterialListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if (xlsResponse.status == 0) {
            List<MaterialBean> materialList = MaterialBean.parseJsonMaterials(mContext, xlsResponse);
            materialAdatapter.setData(materialList);
        } else {
            ToastUtil.showToast(mContext, xlsResponse.message);
        }
    }

    class ManagerTaskRunnable implements Runnable{
        @Override
        public void run() {
            HttpHelper.getInstance().reqMaterialList(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleMaterialListResponse(xlsResponse, call, response);
                }
            });
        }
    }
}
