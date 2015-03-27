package com.yetistep.delivr.model.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
    private BigDecimal amountEarned;
    private Timestamp jobStartedAt;
    private Timestamp completedAt;
    private Integer timeTaken;
    private BigDecimal paidFromCOD;

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

    public BigDecimal getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(BigDecimal amountEarned) {
        this.amountEarned = amountEarned;
    }

    @JsonIgnore
    public Timestamp getJobStartedAt() {
        return jobStartedAt;
    }

    public void setJobStartedAt(Timestamp jobStartedAt) {
        this.jobStartedAt = jobStartedAt;
        setTimeTaken();
    }

    @JsonIgnore
    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
        setTimeTaken();
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    private void setTimeTaken(){
        if(getCompletedAt()!=null && getJobStartedAt() != null){
            Double minuteDiff = DateUtil.getMinDiff(getCompletedAt().getTime(), getJobStartedAt().getTime());
            this.timeTaken = minuteDiff.intValue();
        }
    }

    public BigDecimal getPaidFromCOD() {
        return paidFromCOD;
    }

    public void setPaidFromCOD(BigDecimal paidFromCOD) {
        this.paidFromCOD = paidFromCOD;
    }
}
