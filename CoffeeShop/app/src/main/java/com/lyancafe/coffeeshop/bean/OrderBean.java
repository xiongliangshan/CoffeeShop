package com.lyancafe.coffeeshop.bean;

import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public class OrderBean {

    private long id;                     //订单id
    private String orderSn;              //订单编号
    private long expectedTime;           //期望送达时间
    private long orderTime;              //下单时间
    private long effectBaseTime;         //计算时效的基准时间
    private int providerId;              //咖啡品牌商id
    private String provider;             //咖啡品牌名
    private String recipient;            //收货人名字
    private String address;              //收货人地址
    private String phone;                //收货人联系电话
    private String shopAddress;          //生产点地址
    private String shopPhone;            //生产点联系电话
    private String courierName;          //小哥名字
    private String courierPhone;         //小哥联系电话
    private int status;                  //订单状态
    private String statusName;           //订单状态名
    private boolean issueOrder;          //是否是问题订单
    private String issueRemark;          //问题描述
    private String questionType;         //问题类型
    private String handleType;           //建议处理
    private int instant;                 //预约单 or 尽快送达
    private boolean force;               //是否是客服指派
    private String notes;                //客户下单备注
    private String csrNotes;             //客服备注
    private String platform;             //下单渠道（微信，大众点评，其他）
    private int payChannel;              //支付类型1.现金，2.微信支付，3.支付宝
    private String payChannelStr;        //支付类型名
    private int paid;                    //已付费用
    private long handoverTime;           //实际送达时间
    private String receipt;              //小票编号
    private List<ItemContentBean> items; //购买的咖啡内容列表

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
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

    public long getEffectBaseTime() {
        return effectBaseTime;
    }

    public void setEffectBaseTime(long effectBaseTime) {
        this.effectBaseTime = effectBaseTime;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
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

    public String getStatusName() {
        switch (this.status) {
            case OrderHelper.UNASSIGNED_STATUS:
                this.statusName = "待接单";
                break;
            case OrderHelper.ASSIGNED_STATUS:
                this.statusName = "待取货";
                break;
            case OrderHelper.DELIVERING_STATUS:
                this.statusName = "配送中";
                break;
            case OrderHelper.DELIVERED_STATUS:
                this.statusName = "已完成";
                break;

            default:
                this.statusName = statusName;
                this.statusName = "错误";
                break;
        }
        return statusName;
    }



    public boolean issueOrder() {
        return issueOrder;
    }

    public void setIssueOrder(boolean issueOrder) {
        this.issueOrder = issueOrder;
    }

    public String getIssueRemark() {
        return issueRemark;
    }

    public void setIssueRemark(String issueRemark) {
        this.issueRemark = issueRemark;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public int getInstant() {
        return instant;
    }

    public void setInstant(int instant) {
        this.instant = instant;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
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

    public int getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(int payChannel) {
        this.payChannel = payChannel;
    }

    public String getPayChannelStr() {
        return payChannelStr;
    }

    public void setPayChannelStr(String payChannelStr) {
        this.payChannelStr = payChannelStr;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public long getHandoverTime() {
        return handoverTime;
    }

    public void setHandoverTime(long handoverTime) {
        this.handoverTime = handoverTime;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
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
                ", orderSn='" + orderSn + '\'' +
                ", expectedTime=" + expectedTime +
                ", orderTime=" + orderTime +
                ", effectBaseTime=" + effectBaseTime +
                ", providerId=" + providerId +
                ", provider='" + provider + '\'' +
                ", recipient='" + recipient + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopPhone='" + shopPhone + '\'' +
                ", courierName='" + courierName + '\'' +
                ", courierPhone='" + courierPhone + '\'' +
                ", status=" + status +
                ", statusName='" + statusName + '\'' +
                ", issueOrder=" + issueOrder +
                ", issueRemark='" + issueRemark + '\'' +
                ", questionType='" + questionType + '\'' +
                ", handleType='" + handleType + '\'' +
                ", instant=" + instant +
                ", force=" + force +
                ", notes='" + notes + '\'' +
                ", csrNotes='" + csrNotes + '\'' +
                ", platform='" + platform + '\'' +
                ", payChannel=" + payChannel +
                ", payChannelStr='" + payChannelStr + '\'' +
                ", paid=" + paid +
                ", handoverTime=" + handoverTime +
                ", receipt='" + receipt + '\'' +
                ", items=" + items +
                '}';
    }
}
