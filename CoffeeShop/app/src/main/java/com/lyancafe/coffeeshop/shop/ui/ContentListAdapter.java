package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.MaterialItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder>{


    public List<MaterialItem> list = new ArrayList<>();
    private Context mContext;
    private int selected = -1;

    public ContentListAdapter(List<MaterialItem> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_item,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MaterialItem item = list.get(position);
        holder.nameText.setText(item.getName());
        holder.nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = position;
                notifyDataSetChanged();
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

    public MaterialItem getSelectedItem(){
        if(list.size()>selected && selected!=-1){
            return list.get(selected);
        }else{
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<MaterialItem> list){
        this.list = list;
        notifyDataSetChanged();
        selected = -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public ViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.tv_material_name);

        }
    }
}
