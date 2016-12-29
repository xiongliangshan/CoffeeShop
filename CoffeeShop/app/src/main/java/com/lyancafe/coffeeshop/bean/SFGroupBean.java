package com.lyancafe.coffeeshop.bean;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.Jresp;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SFGroupBean {

    private int id;

    private List<OrderBean> itemGroup;

    public SFGroupBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderBean> getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(List<OrderBean> itemGroup) {
        this.itemGroup = itemGroup;
    }


    @Override
    public String toString() {
        return "SFGroupBean{" +
                "id=" + id +
                ", itemGroup=" + itemGroup +
                '}';
    }

    //解析数据
    public  static List<SFGroupBean> parseJsonGroups(Context context,Jresp resp){
        List<SFGroupBean> sfGroupBeans = new ArrayList<SFGroupBean>();
        if(resp==null || resp.data==null){
            return sfGroupBeans;
        }
        try{
            JSONArray ordersArray= resp.data.optJSONArray("groupList");
            sfGroupBeans = JSON.parseArray(ordersArray.toString(), SFGroupBean.class);
        }catch (JSONException e){
            Log.e("json", e.getMessage());
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return sfGroupBeans;
    }


    //解析数据
    public  static List<SFGroupBean> parseJsonGroups(Context context,XlsResponse resp){
        List<SFGroupBean> sfGroupBeans = new ArrayList<SFGroupBean>();
        if(resp==null || resp.data==null){
            return sfGroupBeans;
        }
        try{
            com.alibaba.fastjson.JSONArray ordersArray = resp.data.getJSONArray("groupList");
            sfGroupBeans = JSON.parseArray(ordersArray.toString(), SFGroupBean.class);
        }catch (JSONException e){
            Log.e("json", e.getMessage());
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return sfGroupBeans;
    }

}
