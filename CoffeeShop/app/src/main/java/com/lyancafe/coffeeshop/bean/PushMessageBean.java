package com.lyancafe.coffeeshop.bean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public String toString() {
        return "PushMessageBean{" +
                "id='" + id + '\'' +
                ", orderId=" + orderId +
                ", eventType=" + eventType +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", openType=" + openType +
                ", title='" + title + '\'' +
                ", userConfirm=" + userConfirm +
                '}';
    }

    public static PushMessageBean parseJsonToMB(String jsonStr){
        PushMessageBean pmb = null;
        try {
            JSONObject obj =  new JSONObject(jsonStr);
            JSONObject customContent = obj.optJSONObject("custom_content");
            String id = customContent.optString("id");
            int eventType = customContent.optInt("eventType");
            long orderId = customContent.optLong("orderId");
            long createTime = customContent.optLong("createTime");

            String description = obj.optString("description");
            int openType = obj.optInt("open_type");
            String title = obj.optString("title");
            int userConfirm = obj.optInt("user_confirm");
            pmb = new PushMessageBean(id,orderId,eventType,createTime,description,openType,title,userConfirm);

        }catch (JSONException e){
            Log.e("getui",""+e.getMessage());
        }
        return pmb;
    }
}
