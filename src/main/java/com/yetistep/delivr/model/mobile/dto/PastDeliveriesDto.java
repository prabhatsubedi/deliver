package com.yetistep.delivr.model.mobile.dto;

import com.yetistep.delivr.enums.JobOrderStatus;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class PastDeliveriesDto {
    private Integer id;
    private Integer orderId;
    private String orderName;
    private JobOrderStatus orderStatus;
    private BigDecimal distanceTravelled;
    private Integer timeTaken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(BigDecimal distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }
}
