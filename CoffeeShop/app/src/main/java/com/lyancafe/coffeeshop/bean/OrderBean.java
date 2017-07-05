package com.lyancafe.coffeeshop.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Administrator on 2015/9/21.
 */
@Entity
public class OrderBean implements Serializable{

    private static final long serialVersionUID = 522216560L;

    @Id
    private long id;                     //订单id

    private String reminder;             //Y:催单，N:不催单

    private String orderSn;              //订单编号

    private int shopOrderNo;             //门店单号

    private boolean orderVip;           //是否是vip订单

    private boolean wxScan;            //是否是到店扫码的单子是否是vip订单

    private boolean isRecipeFittings;  //是否有个性化标签

    private long expectedTime;           //期望送达时间

    private long orderTime;              //下单时间

    private long distributeTime;         //推送给海葵的时间

    private long produceEffect;         //计算生产时效的基准时间

    private String recipient;            //收货人名字

    private String address;              //收货人地址

    private String phone;                //收货人联系电话

    private String courierName;          //小哥名字

    private String courierPhone;         //小哥联系电话

    private int status;                  //订单状态

    private int produceStatus;            //生产状态 4000:待生产  4005:生产中  4010:生产完成

    private boolean issueOrder;          //是否是问题订单

    private int instant;                 //0预约单 or 1尽快送达

    private String notes;                //客户下单备注

    private String csrNotes;             //客服备注

    private String platform;             //下单渠道（微信，大众点评，其他）

    private long handoverTime;           //实际送达时间

    @Transient
    private List<String> feedbackTags;   //评论标签

    private String feedback;             //评论内容

    private int feedbackType;            // 0:没有评价，4:好评，5:差评

    private int deliveryTeam;               //   配送团队 4:lyan 5:qusong 6:wokuaidao 7:sweets 8:美团外卖 9:海葵

    private int platformId;                 //   订单渠道ID  29:美团   其他：我们

    private int mtShopOrderNo;                  //   美团门店单号

    private double orderDistance;       //订单距离

    @Transient
    private List<ItemContentBean> items; //购买的咖啡内容列表

    @Generated(hash = 2073696527)
    public OrderBean(long id, String reminder, String orderSn, int shopOrderNo,
            boolean orderVip, boolean wxScan, boolean isRecipeFittings, long expectedTime,
            long orderTime, long distributeTime, long produceEffect, String recipient,
            String address, String phone, String courierName, String courierPhone, int status,
            int produceStatus, boolean issueOrder, int instant, String notes, String csrNotes,
            String platform, long handoverTime, String feedback, int feedbackType,
            int deliveryTeam, int platformId, int mtShopOrderNo, double orderDistance) {
        this.id = id;
        this.reminder = reminder;
        this.orderSn = orderSn;
        this.shopOrderNo = shopOrderNo;
        this.orderVip = orderVip;
        this.wxScan = wxScan;
        this.isRecipeFittings = isRecipeFittings;
        this.expectedTime = expectedTime;
        this.orderTime = orderTime;
        this.distributeTime = distributeTime;
        this.produceEffect = produceEffect;
        this.recipient = recipient;
        this.address = address;
        this.phone = phone;
        this.courierName = courierName;
        this.courierPhone = courierPhone;
        this.status = status;
        this.produceStatus = produceStatus;
        this.issueOrder = issueOrder;
        this.instant = instant;
        this.notes = notes;
        this.csrNotes = csrNotes;
        this.platform = platform;
        this.handoverTime = handoverTime;
        this.feedback = feedback;
        this.feedbackType = feedbackType;
        this.deliveryTeam = deliveryTeam;
        this.platformId = platformId;
        this.mtShopOrderNo = mtShopOrderNo;
        this.orderDistance = orderDistance;
    }

    @Generated(hash = 1725534308)
    public OrderBean() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReminder() {
        return this.reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getOrderSn() {
        return this.orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getShopOrderNo() {
        return this.shopOrderNo;
    }

    public void setShopOrderNo(int shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public boolean getOrderVip() {
        return this.orderVip;
    }

    public void setOrderVip(boolean orderVip) {
        this.orderVip = orderVip;
    }

    public boolean getWxScan() {
        return this.wxScan;
    }

    public void setWxScan(boolean wxScan) {
        this.wxScan = wxScan;
    }

    public boolean getIsRecipeFittings() {
        return this.isRecipeFittings;
    }

    public void setIsRecipeFittings(boolean isRecipeFittings) {
        this.isRecipeFittings = isRecipeFittings;
    }

    public long getExpectedTime() {
        return this.expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    public long getOrderTime() {
        return this.orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getProduceEffect() {
        return this.produceEffect;
    }

    public void setProduceEffect(long produceEffect) {
        this.produceEffect = produceEffect;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourierName() {
        return this.courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierPhone() {
        return this.courierPhone;
    }

    public void setCourierPhone(String courierPhone) {
        this.courierPhone = courierPhone;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProduceStatus() {
        return this.produceStatus;
    }

    public void setProduceStatus(int produceStatus) {
        this.produceStatus = produceStatus;
    }

    public boolean getIssueOrder() {
        return this.issueOrder;
    }

    public void setIssueOrder(boolean issueOrder) {
        this.issueOrder = issueOrder;
    }

    public int getInstant() {
        return this.instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCsrNotes() {
        return this.csrNotes;
    }

    public void setCsrNotes(String csrNotes) {
        this.csrNotes = csrNotes;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getHandoverTime() {
        return this.handoverTime;
    }

    public void setHandoverTime(long handoverTime) {
        this.handoverTime = handoverTime;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getFeedbackType() {
        return this.feedbackType;
    }

    public void setFeedbackType(int feedbackType) {
        this.feedbackType = feedbackType;
    }

    public int getDeliveryTeam() {
        return this.deliveryTeam;
    }

    public void setDeliveryTeam(int deliveryTeam) {
        this.deliveryTeam = deliveryTeam;
    }

    public int getPlatformId() {
        return this.platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getMtShopOrderNo() {
        return this.mtShopOrderNo;
    }

    public void setMtShopOrderNo(int mtShopOrderNo) {
        this.mtShopOrderNo = mtShopOrderNo;
    }

    public double getOrderDistance() {
        return this.orderDistance;
    }

    public void setOrderDistance(double orderDistance) {
        this.orderDistance = orderDistance;
    }

    public boolean isOrderVip() {
        return orderVip;
    }

    public boolean isWxScan() {
        return wxScan;
    }

    public boolean isRecipeFittings() {
        return isRecipeFittings;
    }

    public void setRecipeFittings(boolean recipeFittings) {
        isRecipeFittings = recipeFittings;
    }

    public boolean issueOrder() {
        return issueOrder;
    }

    public List<String> getFeedbackTags() {
        return feedbackTags;
    }

    public void setFeedbackTags(List<String> feedbackTags) {
        this.feedbackTags = feedbackTags;
    }

    public void setItems(List<ItemContentBean> items) {
        this.items = items;
    }

    public List<ItemContentBean> getItems() {
        return items;
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
                ", distributeTime=" + distributeTime +
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

    public long getDistributeTime() {
        return this.distributeTime;
    }

    public void setDistributeTime(long distributeTime) {
        this.distributeTime = distributeTime;
    }
}
