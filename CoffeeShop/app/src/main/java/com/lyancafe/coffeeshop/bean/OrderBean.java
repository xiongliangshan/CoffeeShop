package com.lyancafe.coffeeshop.bean;

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


}
