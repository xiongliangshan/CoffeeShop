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
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final UserBean user = itemList.get(i);
        viewHolder.flagTxt.setVisibility(user.isManager() ? View.VISIBLE : View.INVISIBLE);
        if(user.isOnDuty()){
            viewHolder.image.setImageResource(R.mipmap.icon_user_working);
            viewHolder.actionBtn.setText("下班");
            viewHolder.actionBtn.setBackgroundResource(R.drawable.bg_produce_btn_red);
        }else{
            viewHolder.image.setImageResource(R.mipmap.icon_user_not_working);
            viewHolder.actionBtn.setText("上班");
            viewHolder.actionBtn.setBackgroundResource(R.drawable.bg_produce_btn);
        }
        viewHolder.nameTxt.setText(user.getName());
        viewHolder.phoneTxt.setText(user.getPhone());
        viewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = user.isOnDuty()?"OFF":"ON";
                new PunchQry(user.getId(),value,context).doRequest();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return itemList.size();
    }

    public void setData(List<UserBean> itemList){
        this.itemList = itemList;
        notifyDataSetChanged();
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

    //咖啡师上下班打卡接口
    class PunchQry implements Qry {

        private int baristaId;
        private String value;
        private Context context;


        public PunchQry(int baristaId, String value, Context context) {
            this.baristaId = baristaId;
            this.value = value;
            this.context = context;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/barista/"+baristaId+"/onduty?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("value",value);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "PunchQry:resp  =" + resp);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status==0){
                ToastUtil.showToast(context,"打卡成功");
                for(int i = 0;i<itemList.size();i++){
                    if(baristaId==itemList.get(i).getId()){
                        itemList.get(i).setOnDuty(value.equals("ON")?true:false);
                        notifyItemChanged(i);
                        break;
                    }
                }
                if(resp.data.optBoolean("isMaster")){
                    //主咖啡师下班，退出登录
                    ToastUtil.showToast(context, "主咖啡师下班，退出登录");
                }

            }else{
                ToastUtil.showToast(context,resp.message);
            }
        }
    }
}
