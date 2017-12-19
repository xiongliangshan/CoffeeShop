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
}
