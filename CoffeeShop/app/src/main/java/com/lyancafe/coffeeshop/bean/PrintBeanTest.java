package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2018/1/22.
 */

public class PrintBeanTest {

    private String name;
    private String date;


    public PrintBeanTest(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PrintBeanTest{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
