package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2015/8/20.
 */
public class PushMessageBean {
    private String id;
    private long orderId;
    private int eventType;
    private long createTime;
    private String description;
    private int openType;
    private String title;
    private int userConfirm;


    public PushMessageBean(String id, long orderId, int eventType, long createTime, String description, int openType, String title, int userConfirm) {
        this.id = id;
        this.orderId = orderId;
        this.eventType = eventType;
        this.createTime = createTime;
        this.description = description;
        this.openType = openType;
        this.title = title;
        this.userConfirm = userConfirm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserConfirm() {
        return userConfirm;
    }

    public void setUserConfirm(int userConfirm) {
        this.userConfirm = userConfirm;
    }
}
