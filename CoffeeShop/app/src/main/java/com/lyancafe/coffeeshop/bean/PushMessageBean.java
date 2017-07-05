package com.lyancafe.coffeeshop.bean;

import com.google.gson.Gson;
import com.lyancafe.coffeeshop.utils.LogUtil;

/**
 * Created by Administrator on 2015/8/20.
 */
public class PushMessageBean {
    private int id;   //消息 id;
    private String title;  //消息标题
    private long orderId;  //订单id（有则传，没有则传0）
    private int eventType;   //通知事件类型 1：新订单  10:小哥上报问题 11:问题已解决 16:订单撤回 20:催单
    private String content; //消息内容

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PushMessageBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", orderId=" + orderId +
                ", eventType=" + eventType +
                ", content='" + content + '\'' +
                '}';
    }

    public static PushMessageBean convertToBean(String jsonStr){
        LogUtil.d("gson","before : "+jsonStr);
        Gson gson = new Gson();
        PushMessageBean pmb  = gson.fromJson(jsonStr,PushMessageBean.class);
        LogUtil.d("gson","after :  pmb = "+pmb.toString());
        return pmb;
    }

}
