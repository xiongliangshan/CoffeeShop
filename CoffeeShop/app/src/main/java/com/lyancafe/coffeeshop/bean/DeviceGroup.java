package com.lyancafe.coffeeshop.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */

public class DeviceGroup {

    /**
     * 门店Id
     */
    private int shopId;

    /**
     * 心跳类型(0:启动，1:运行)
     */
    private int type;

    /**
     * 具体的设备集合
     */
    private List<Device> group;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Device> getGroup() {
        return group;
    }

    public void setGroup(List<Device> group) {
        this.group = group;
    }
}
