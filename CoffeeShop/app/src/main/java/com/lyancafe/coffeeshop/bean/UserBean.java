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
 * Created by Administrator on 2015/10/21.
 */
public class UserBean {

    private int id;  //咖啡师id
    private boolean manager; //是否是店长，true:是店长 false:不是店长
    private boolean onDuty;   //上下班的状态，0:下班  1：上班
    private String name; //咖啡师的名字
    private String phone; //咖啡师的手机号码


    public UserBean() {
    }

    public UserBean(int id, boolean manager, boolean onDuty, String name, String phone) {
        this.id = id;
        this.manager = manager;
        this.onDuty = onDuty;
        this.name = name;
        this.phone = phone;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //解析数据
    public  static List<UserBean> parseJsonUsers(Context context,Jresp resp){
        List<UserBean> userBeans = new ArrayList<UserBean>();

        try{
            JSONArray ordersArray= resp.data.optJSONArray("baristList");
            userBeans = JSON.parseArray(ordersArray.toString(), UserBean.class);
        }catch (JSONException e){
            Log.e("xiong", e.getMessage());
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return userBeans;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", manager=" + manager +
                ", onDuty=" + onDuty +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
