package com.lyancafe.coffeeshop.bean;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/12/21.
 */

public class Product implements Comparable<Product>{

    private String name;
    private int count;
    private int produceProcess; //0 ：null,1：咖啡师生产,咖啡师出品,2：饮品师生产，饮品师出品,3：咖啡师生产，饮品师生产，饮品师出品
    private boolean isCustom;//是否有定制口味

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public int getProduceProcess() {
        return produceProcess;
    }

    public void setProduceProcess(int produceProcess) {
        this.produceProcess = produceProcess;
    }


    @Override
    public int compareTo(@NonNull Product o) {
        if (o.getCount() != this.getCount()) {
            return o.getCount() - this.getCount();
        } else {
            return o.getName().compareTo(this.getName());
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", produceProcess=" + produceProcess +
                ", isCustom=" + isCustom +
                '}';
    }
}
