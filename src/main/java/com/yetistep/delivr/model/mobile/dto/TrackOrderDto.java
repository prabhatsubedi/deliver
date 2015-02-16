package com.yetistep.delivr.model.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yetistep.delivr.enums.JobOrderStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/16/15
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrackOrderDto {
    private Integer orderId;
    /*Courier Boy Section*/
    private String courierBoyName;
    private String courierBoyImage;
    private String courierBoyContactNo;
    private String courierBoyLatitude;
    private String courierBoyLongitude;
    private Integer vehicleType;
    /*Store section*/
    private String storeLatitude;
    private String storeLongitude;
    /*Delivery address section*/
    private String deliveryLatitude;
    private String deliveryLongitude;
    /*Order status section*/
    private JobOrderStatus orderStatus;
    private String timeDisplayMessage;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCourierBoyName() {
        return courierBoyName;
    }

    public void setCourierBoyName(String courierBoyName) {
        this.courierBoyName = courierBoyName;
    }

    public String getCourierBoyImage() {
        return courierBoyImage;
    }

    public void setCourierBoyImage(String courierBoyImage) {
        this.courierBoyImage = courierBoyImage;
    }

    public String getCourierBoyContactNo() {
        return courierBoyContactNo;
    }

    public void setCourierBoyContactNo(String courierBoyContactNo) {
        this.courierBoyContactNo = courierBoyContactNo;
    }

    public String getCourierBoyLatitude() {
        return courierBoyLatitude;
    }

    public void setCourierBoyLatitude(String courierBoyLatitude) {
        this.courierBoyLatitude = courierBoyLatitude;
    }

    public String getCourierBoyLongitude() {
        return courierBoyLongitude;
    }

    public void setCourierBoyLongitude(String courierBoyLongitude) {
        this.courierBoyLongitude = courierBoyLongitude;
    }

    @JsonIgnore
    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(String deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public String getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(String deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTimeDisplayMessage() {
        return timeDisplayMessage;
    }

    public void setTimeDisplayMessage(String timeDisplayMessage) {
        this.timeDisplayMessage = timeDisplayMessage;
    }
}
