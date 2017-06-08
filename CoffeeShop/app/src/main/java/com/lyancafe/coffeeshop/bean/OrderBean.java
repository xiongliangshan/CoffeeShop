package com.lyancafe.coffeeshop.bean;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderBean implements Serializable{

    private static final String TAG = "OrderBean";

     long id;                     //订单id
     String reminder;             //Y:催单，N:不催单
     String orderSn;              //订单编号
     int shopOrderNo;             //门店单号
     boolean orderVip;           //是否是vip订单
     boolean wxScan;            //是否是到店扫码的单子是否是vip订单
     boolean isRecipeFittings;  //是否有个性化标签
     long expectedTime;           //期望送达时间
     long orderTime;              //下单时间
     long produceEffect;         //计算生产时效的基准时间
     String recipient;            //收货人名字
     String address;              //收货人地址
     String phone;                //收货人联系电话
     String courierName;          //小哥名字
     String courierPhone;         //小哥联系电话
     int status;                  //订单状态
     int produceStatus;            //生产状态 4000:待生产  4005:生产中  4010:生产完成
     boolean issueOrder;          //是否是问题订单
     int instant;                 //0预约单 or 1尽快送达
     String notes;                //客户下单备注
     String csrNotes;             //客服备注
     String platform;             //下单渠道（微信，大众点评，其他）
     long handoverTime;           //实际送达时间
     List<String> feedbackTags;   //评论标签
     String feedback;             //评论内容
     int feedbackType;            // 0:没有评价，4:好评，5:差评
     int deliveryTeam;               //   配送团队 4:lyan 5:qusong 6:wokuaidao 7:sweets 8:美团外卖 9:海葵
     int platformId;                 //   订单渠道ID  29:美团   其他：我们
     int mtShopOrderNo;                  //   美团门店单号
     double orderDistance;           //订单距离
     List<ItemContentBean> items; //购买的咖啡内容列表



    public OrderBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(int shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public boolean isOrderVip() {
        return orderVip;
    }

    public void setOrderVip(boolean orderVip) {
        this.orderVip = orderVip;
    }

    public boolean isWxScan() {
        return wxScan;
    }

    public void setWxScan(boolean wxScan) {
        this.wxScan = wxScan;
    }

    public boolean isRecipeFittings() {
        return isRecipeFittings;
    }

    public void setIsRecipeFittings(boolean isRecipeFittings) {
        this.isRecipeFittings = isRecipeFittings;
    }


    public long getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getProduceEffect() {
        return produceEffect;
    }

    public void setProduceEffect(long produceEffect) {
        this.produceEffect = produceEffect;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierPhone() {
        return courierPhone;
    }

    public void setCourierPhone(String courierPhone) {
        this.courierPhone = courierPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProduceStatus() {
        return produceStatus;
    }

    public void setProduceStatus(int produceStatus) {
        this.produceStatus = produceStatus;
    }


    public boolean issueOrder() {
        return issueOrder;
    }

    public void setIssueOrder(boolean issueOrder) {
        this.issueOrder = issueOrder;
    }


    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCsrNotes() {
        return csrNotes;
    }

    public void setCsrNotes(String csrNotes) {
        this.csrNotes = csrNotes;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getHandoverTime() {
        return handoverTime;
    }

    public void setHandoverTime(long handoverTime) {
        this.handoverTime = handoverTime;
    }

    public List<String> getFeedbackTags() {
        return feedbackTags;
    }

    public void setFeedbackTags(List<String> feedbackTags) {
        this.feedbackTags = feedbackTags;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(int feedbackType) {
        this.feedbackType = feedbackType;
    }

    public int getDeliveryTeam() {
        return deliveryTeam;
    }

    public void setDeliveryTeam(int deliveryTeam) {
        this.deliveryTeam = deliveryTeam;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getMtShopOrderNo() {
        return mtShopOrderNo;
    }

    public void setMtShopOrderNo(int mtShopOrderNo) {
        this.mtShopOrderNo = mtShopOrderNo;
    }

    public double getOrderDistance() {
        return orderDistance;
    }

    public void setOrderDistance(double orderDistance) {
        this.orderDistance = orderDistance;
    }

    public List<ItemContentBean> getItems() {
        return items;
    }

    public void setItems(List<ItemContentBean> items) {
        this.items = items;
    }



    @Override
    public String toString() {
        return "OrderBean{" +
                "id=" + id +
                ", reminder='" + reminder + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", shopOrderNo=" + shopOrderNo +
                ", orderVip=" + orderVip +
                ", wxScan=" + wxScan +
                ", isRecipeFittings=" + isRecipeFittings +
                ", expectedTime=" + expectedTime +
                ", orderTime=" + orderTime +
                ", produceEffect=" + produceEffect +
                ", recipient='" + recipient + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", courierName='" + courierName + '\'' +
                ", courierPhone='" + courierPhone + '\'' +
                ", status=" + status +
                ", produceStatus=" + produceStatus +
                ", issueOrder=" + issueOrder +
                ", instant=" + instant +
                ", notes='" + notes + '\'' +
                ", csrNotes='" + csrNotes + '\'' +
                ", platform='" + platform + '\'' +
                ", handoverTime=" + handoverTime +
                ", feedbackTags=" + feedbackTags +
                ", feedback='" + feedback + '\'' +
                ", feedbackType=" + feedbackType +
                ", deliveryTeam=" + deliveryTeam +
                ", platformId=" + platformId +
                ", mtShopOrderNo=" + mtShopOrderNo +
                ", orderDistance=" + orderDistance +
                ", items=" + items +
                '}';
    }
}
