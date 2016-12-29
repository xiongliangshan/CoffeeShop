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
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.PrintHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.utils.Urls;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MaterialFragment extends Fragment {

    private static final String TAG ="MaterialFragment";
    private Context mContext;
    private View mContentView;
    private TextView printPasterText;
    private TextView printMaterialText;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialAdatapter materialAdatapter;
    private ContentLoadingProgressBar clpBar;
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
        mContentView = inflater.inflate(R.layout.fragment_material,container,false);
        initView();
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        new MaterialListQry(mContext).doRequest();
    }

    private void initView(){
        printPasterText = (TextView) mContentView.findViewById(R.id.tv_print_paster);
        printPasterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintHelper.getInstance().printPasterSmall();
            }
        });
        printMaterialText = (TextView) mContentView.findViewById(R.id.tv_print_material);
        printMaterialText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始打印
                if(toPrintMaterial!=null){
                    PrintHelper.getInstance().printMaterialBig(toPrintMaterial);
                }

            }
        });

        clpBar = (ContentLoadingProgressBar) mContentView.findViewById(R.id.progress_bar);

        recyclerView= (RecyclerView) mContentView.findViewById(R.id.rv_material);
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


    //物料列表接口
    class MaterialListQry implements Qry {

        private Context context;

        public MaterialListQry(Context context) {
            this.context = context;
        }

        @Override
        public void doRequest() {
            clpBar.show();
            LoginBean loginBean = LoginHelper.getLoginBean(context);
            String token = loginBean.getToken();
            int shopId = loginBean.getShopId();
            String url = Urls.BASE_URL+shopId+"/supplies?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.GET, url, params), context, this, false);
        }

        @Override
        public void showResult(Jresp resp) {
            clpBar.hide();
            if(resp==null){
                Log.e(TAG, "MaterialListQry:resp  =" + resp);
                return;
            }
            Log.d(TAG, "BaristasListQry:resp  =" + resp);
            if(resp.status==0){
                List<MaterialBean> materialList = MaterialBean.parseJsonMaterials(mContext,resp);
                materialAdatapter.setData(materialList);
            }else{
                ToastUtil.showToast(mContext,resp.message);
            }
        }
    }
}
