package com.lyancafe.coffeeshop.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EvaluationBean {

    private int id;              	 //评价id
    private int orderId;       		 //订单id
    private int deliveryParameter;   // 配送评价 1:非常差，2:很差，3:一般，4:很好，5:非常好
    private List<String> tags; 		 //标签
    private Map<String,Integer>  productTaste; //口味星级 String是商品名字，Integer是星级
    private String content;			//评价内容
    private OrderBean orderbean;	//orderbean对象

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getDeliveryParameter() {
        return deliveryParameter;
    }

    public void setDeliveryParameter(int deliveryParameter) {
        this.deliveryParameter = deliveryParameter;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, Integer> getProductTaste() {
        return productTaste;
    }

    public void setProductTaste(Map<String, Integer> productTaste) {
        this.productTaste = productTaste;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OrderBean getOrderbean() {
        return orderbean;
    }

    public void setOrderbean(OrderBean orderbean) {
        this.orderbean = orderbean;
    }

    @Override
    public String toString() {
        return "EvaluationBean{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", deliveryParameter=" + deliveryParameter +
                ", tags=" + tags +
                ", productTaste=" + productTaste +
                ", content='" + content + '\'' +
                ", orderbean=" + orderbean +
                '}';
    }
}
