package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/21.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    private List<UserBean> itemList = new ArrayList<UserBean>();
    private Context context;

    public RecyclerAdapter(List<UserBean> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG,"on--Create--ViewHolder");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.d(TAG,"on--Bind--ViewHolder");
        viewHolder.flagTxt.setVisibility(View.VISIBLE);
        viewHolder.image.setImageResource(R.mipmap.icon_user_working);
        viewHolder.nameTxt.setText("熊良山");
        viewHolder.phoneTxt.setText("18217131583");
        viewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context,"我上班了");
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"getItemCount");
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView flagTxt;
        public ImageView image;
        public TextView nameTxt;
        public TextView phoneTxt;
        public Button actionBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            flagTxt  = (TextView) itemView.findViewById(R.id.identity_flag);
            image = (ImageView) itemView.findViewById(R.id.user_icon);
            nameTxt = (TextView) itemView.findViewById(R.id.user_name);
            phoneTxt = (TextView) itemView.findViewById(R.id.user_phone);
            actionBtn = (Button) itemView.findViewById(R.id.btn_action);
        }
    }
}
