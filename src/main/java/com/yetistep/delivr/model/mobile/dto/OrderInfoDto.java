package com.yetistep.delivr.model.mobile.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.util.JsonDateSerializer;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/5/15
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderInfoDto {
    private Integer id;
    private String orderName;
    private JobOrderStatus orderStatus;
    private BigDecimal customerChargeableDistance;
    private BigDecimal systemChargeableDistance;
    private Timestamp orderDate;
    private Timestamp orderAcceptedAt;
    private Integer assignedTime;
    private Integer remainingTime;
    private Integer elapsedTime;
    private BigDecimal paidToCourier;
    private Integer priority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getCustomerChargeableDistance() {
        return customerChargeableDistance;
    }

    public void setCustomerChargeableDistance(BigDecimal customerChargeableDistance) {
        this.customerChargeableDistance = customerChargeableDistance;
    }

    public BigDecimal getSystemChargeableDistance() {
        return systemChargeableDistance;
    }

    public void setSystemChargeableDistance(BigDecimal systemChargeableDistance) {
        this.systemChargeableDistance = systemChargeableDistance;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getOrderAcceptedAt() {
        return orderAcceptedAt;
    }

    public void setOrderAcceptedAt(Timestamp orderAcceptedAt) {
        this.orderAcceptedAt = orderAcceptedAt;
    }

    public Integer getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(Integer assignedTime) {
        this.assignedTime = assignedTime;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public BigDecimal getPaidToCourier() {
        return paidToCourier;
    }

    public void setPaidToCourier(BigDecimal paidToCourier) {
        this.paidToCourier = paidToCourier;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
