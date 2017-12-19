package com.lyancafe.coffeeshop.bean;

/**
 * Created by Administrator on 2017/12/19.
 */

public class Device {

    /**
     * 设备类型(0:咖啡屋PAD，1:大标签打印机，2:小标签打印机)
     */
    private int deviceType;

    /**
     * 硬件型号
     */
    private String hardVersion;

    /**
     * 安卓系统版本号
     */
    private String syssoftVersion;

    /**
     * 咖啡屋App版本号
     */
    private String appsoftVersion;

    /**
     * 设备状态目前只对打印机（100：正常，200：ping不通，101：纸仓打开，102：纸张错误，104：缺纸，108：碳带错误，110：暂停）
     */
    private int status;


    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getSyssoftVersion() {
        return syssoftVersion;
    }

    public void setSyssoftVersion(String syssoftVersion) {
        this.syssoftVersion = syssoftVersion;
    }

    public String getAppsoftVersion() {
        return appsoftVersion;
    }

    public void setAppsoftVersion(String appsoftVersion) {
        this.appsoftVersion = appsoftVersion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
