package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/5/17.
 */

public class MaterialItem {

    private int id;   //物料id
    private String name; //物料名
    private int overdueTime; //过期时间(天)

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

    public int getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(int overdueTime) {
        this.overdueTime = overdueTime;
    }

    @Override
    public String toString() {
        return "MaterialItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", overdueTime=" + overdueTime +
                '}';
    }
}
