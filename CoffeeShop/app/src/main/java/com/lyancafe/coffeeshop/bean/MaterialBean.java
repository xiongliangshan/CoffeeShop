package com.lyancafe.coffeeshop.bean;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class MaterialBean {

    private int id;
    private String name;
    private int overdueDays;

    public MaterialBean() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }

    @Override
    public String toString() {
        return "MaterialBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", overdueDays=" + overdueDays +
                '}';
    }

    //解析数据
    public  static List<MaterialBean> parseJsonMaterials(Context context,XlsResponse resp){
        List<MaterialBean> materialBeans = new ArrayList<MaterialBean>();
        if(resp==null || resp.data==null){
            return materialBeans;
        }
        try{
            JSONArray ordersArray= resp.data.getJSONArray("supplies");
            materialBeans = JSON.parseArray(ordersArray.toString(), MaterialBean.class);
        }catch (JSONException e){
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return materialBeans;
    }
}
