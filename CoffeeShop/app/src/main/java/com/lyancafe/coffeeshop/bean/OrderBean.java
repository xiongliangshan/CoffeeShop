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

    private long id;                     //订单id
    private int groupId;                 //顺风单组的id
    private String orderSn;              //订单编号
    private int shopOrderNo;             //门店单号
    private boolean orderVip;           //是否是vip订单
    private boolean wxScan;            //是否是到店扫码的单子是否是vip订单
    private boolean isRecipeFittings;  //是否有个性化标签
    private int gift;                  //2:此单有礼品  非2：此单无礼品
    private String wishes;             //礼品代号
    private long expectedTime;           //期望送达时间
    private long orderTime;              //下单时间
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
    private List<String> feedbackTags;   //评论标签
    private String feedback;             //评论内容
    private int feedbackType;            // 0:没有评价，1:好评，2:差评
    private int deliveryTeam;               //   配送团队 4:lyan 5:qusong 6:wokuaidao 7:sweets 8:美团外卖
    private int platformId;                 //   订单渠道ID  29:美团   其他：我们
    private int mtShopOrderNo;                  //   美团门店单号
    private List<ItemContentBean> items; //购买的咖啡内容列表



    public OrderBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public String getWishes() {
        return wishes;
    }

    public void setWishes(String wishes) {
        this.wishes = wishes;
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

    public List<ItemContentBean> getItems() {
        return items;
    }

    public void setItems(List<ItemContentBean> items) {
        this.items = items;
    }




    //解析数据
    public  static List<OrderBean> parseJsonOrders(Context context,XlsResponse resp){
        List<OrderBean> orderBeans = new ArrayList<OrderBean>();
        if(resp==null || resp.data==null){
            return orderBeans;
        }
        try{
            com.alibaba.fastjson.JSONArray ordersArray= resp.data.getJSONArray("orders");
            if(ordersArray!=null){
                orderBeans = JSON.parseArray(ordersArray.toString(), OrderBean.class);
            }
        }catch (JSONException e){
            Log.e(TAG,e.getMessage());
            ToastUtil.showToast(context, R.string.parse_json_fail);
        }
        return orderBeans;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", orderSn='" + orderSn + '\'' +
                ", shopOrderNo=" + shopOrderNo +
                ", orderVip=" + orderVip +
                ", wxScan=" + wxScan +
                ", isRecipeFittings=" + isRecipeFittings +
                ", gift=" + gift +
                ", wishes='" + wishes + '\'' +
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
                ", items=" + items +
                '}';
    }
}
