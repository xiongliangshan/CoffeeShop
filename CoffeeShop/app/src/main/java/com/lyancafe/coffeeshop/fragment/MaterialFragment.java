package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.MaterialAdatapter;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MaterialFragment extends Fragment {

    private static final String TAG ="MaterialFragment";
    private Context mContext;
    @BindView(R.id.tv_print_paster) TextView printPasterText;
    @BindView(R.id.tv_print_material) TextView printMaterialText;
    @BindView(R.id.rv_material) RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialAdatapter materialAdatapter;
    @BindView(R.id.progress_bar) ContentLoadingProgressBar clpBar;
    private MaterialBean toPrintMaterial;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_material,container,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        HttpHelper.getInstance().reqMaterialList(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleMaterialListResponse(xlsResponse, call, response);
            }
        });
    }

    @OnClick({R.id.tv_print_paster,R.id.tv_print_material})
    void onClick(View v){
        switch (v.getId()){
            case R.id.tv_print_paster:
                PrintHelper.getInstance().printPasterSmall();
                break;
            case R.id.tv_print_material:
                //开始打印
                if(toPrintMaterial!=null){
                    PrintHelper.getInstance().printMaterialBig(toPrintMaterial);
                }
                break;
        }
    }

    private void handleMaterialListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<MaterialBean> materialList = MaterialBean.parseJsonMaterials(mContext,xlsResponse);
            materialAdatapter.setData(materialList);
        }else{
            ToastUtil.showToast(mContext,xlsResponse.message);
        }
    }

    private void initView(){
        layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<MaterialBean> itemList = new ArrayList<>();
        materialAdatapter = new MaterialAdatapter(itemList, mContext);
        recyclerView.setAdapter(materialAdatapter);
    }



    @Subscribe
    public void OnMessageEvent(MaterialSelectEvent event){
        if(event.selected>=0){
            toPrintMaterial = event.materialBean;
            printMaterialText.setEnabled(true);
            printMaterialText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_black_circle));
            printMaterialText.setTextColor(mContext.getResources().getColor(R.color.white_font));
        }else{
            toPrintMaterial = null;
            printMaterialText.setEnabled(false);
            printMaterialText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_white_circle));
            printMaterialText.setTextColor(mContext.getResources().getColor(R.color.text_black));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
