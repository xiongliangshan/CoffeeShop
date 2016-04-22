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
 * Created by Administrator on 2016/4/9.
 */
public class MaterialBean {

    private int id;
    private String name;

    public MaterialBean() {
    }

    public MaterialBean(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "MaterialBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    //解析数据
    public  static List<MaterialBean> parseJsonMaterials(Context context,Jresp resp){
        List<MaterialBean> materialBeans = new ArrayList<MaterialBean>();
        if(resp==null || resp.data==null){
            return materialBeans;
        }
        try{
            JSONArray ordersArray= resp.data.optJSONArray("supplies");
            materialBeans = JSON.parseArray(ordersArray.toString(), MaterialBean.class);
        }catch (JSONException e){
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return materialBeans;
    }
}
