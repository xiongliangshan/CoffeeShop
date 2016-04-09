package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MaterialAdatapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MaterialBean> itemlist;
    private Context mContext;

    public MaterialAdatapter(ArrayList<MaterialBean> itemlist,Context context) {
        this.itemlist = itemlist;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setData(ArrayList<MaterialBean> list){
        if(list==null){
            return;
        }
        this.itemlist = list;
        notifyDataSetChanged();
    }
}
