package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.MaterialBean;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.event.MaterialSelectEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MaterialAdatapter extends RecyclerView.Adapter<MaterialAdatapter.MaterialViewHolder> {

    private List<MaterialBean> itemlist = new ArrayList<>();
    private Context mContext;
    private int selected = -1;

    public MaterialAdatapter(ArrayList<MaterialBean> itemlist,Context context) {
        this.itemlist = itemlist;
        this.mContext = context;
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_item,parent,false);
        return new MaterialViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, final int position) {
        final MaterialBean materialBean = itemlist.get(position);
        holder.nameText.setText(materialBean.getName());
        holder.nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = position;
                notifyDataSetChanged();
                EventBus.getDefault().post(new MaterialSelectEvent(selected,materialBean));
            }
        });
        if(position==selected){
            holder.nameText.setTextColor(mContext.getResources().getColor(R.color.white_font));
            holder.nameText.setBackgroundResource(R.drawable.bg_corner_black_no_stroke);
        }else{
            holder.nameText.setTextColor(mContext.getResources().getColor(R.color.text_black));
            holder.nameText.setBackgroundResource(R.drawable.bg_corner_black_stroke);
        }
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<MaterialBean> list){
        if(list==null){
            return;
        }
        this.itemlist = list;
        notifyDataSetChanged();
        selected = -1;
        EventBus.getDefault().post(new MaterialSelectEvent(selected, null));
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder{

        public TextView nameText;

        public MaterialViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.tv_material_name);
        }
    }
}
