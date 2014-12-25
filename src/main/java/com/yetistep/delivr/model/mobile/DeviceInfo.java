package com.yetistep.delivr.model.mobile;

import com.google.gson.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/2/14
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceInfo {
    private String uuid;
    private String brand;
    private String deviceToken;

    public DeviceInfo() {
    }

    public DeviceInfo(String uuid, String brand, String deviceToken) {
        this.uuid = uuid;
        this.brand = brand;
        this.deviceToken = deviceToken;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


}
