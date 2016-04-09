package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/9.
 */
public class PrintFragment extends Fragment {

    private static final String TAG ="PrintFragment";
    private Context mContext;
    private View mContentView;
    private TextView printPasterText;
    private TextView printMaterialText;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialAdatapter materialAdatapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_print,container,false);
        initView();
        return mContentView;
    }

    private void initView(){
        printPasterText = (TextView) mContentView.findViewById(R.id.tv_print_paster);
        printMaterialText = (TextView) mContentView.findViewById(R.id.tv_print_material);

        recyclerView= (RecyclerView) mContentView.findViewById(R.id.rv_material);
        layoutManager = new GridLayoutManager(mContext,4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpaceItemDecoration(OrderHelper.dip2Px(32, mContext)));
        ArrayList<MaterialBean> itemList = new ArrayList<>();
        materialAdatapter = new MaterialAdatapter(itemList,mContext);
        recyclerView.setAdapter(materialAdatapter);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //设置RecyclerView item之间的间距
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildLayoutPosition(view) != 0)
                outRect.left = space;
        }
    }

    //咖啡师列表接口
    class BaristasListQry implements Qry {

        private Context context;

        public BaristasListQry(Context context) {
            this.context = context;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/baristas?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, true);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "BaristasListQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }

            materialAdatapter.setData(null);
        }
    }
}
