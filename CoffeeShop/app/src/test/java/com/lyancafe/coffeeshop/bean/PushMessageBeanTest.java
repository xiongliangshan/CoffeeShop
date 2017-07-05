package com.lyancafe.coffeeshop.bean;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/7/5.
 */
public class PushMessageBeanTest {
    @Test
    public void convertToBean() throws Exception {
        String json = "{\"content\":\"新订单到达：64892\",\"id\":0,\"title\":\"连咖啡咖啡屋消息\",\"eventType\":1,\"orderId\":64892}";
        PushMessageBean pmb = PushMessageBean.convertToBean(json);

        assertEquals(pmb.getEventType(),1);
        assertEquals(pmb.getOrderId(),64892);
    }

}