package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/5/17.
 */

public class BaseEntity<T> {

    private int status;   //返回的状态码
    private String message; //返回的文字消息
    private T data;         //返回的数据

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
