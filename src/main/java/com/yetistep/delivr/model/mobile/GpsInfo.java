package com.yetistep.delivr.model.mobile;


/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/2/14
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class GpsInfo {
    private String latitude;
    private String longitude;

    public GpsInfo() {
    }

    public GpsInfo(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
