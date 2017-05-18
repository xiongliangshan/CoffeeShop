package com.lyancafe.coffeeshop.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */

public class Material {

    private int id;     //类型id
    private String category; //分类名
    private List<MaterialItem> items; //分类列表


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MaterialItem> getItems() {
        return items;
    }

    public void setItems(List<MaterialItem> items) {
        this.items = items;
    }


    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", items=" + items +
                '}';
    }
}
