package com.yetistep.delivr.model.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yetistep.delivr.enums.JobOrderStatus;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/4/15
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyOrderDto {
    private Integer orderId;
    private String brandLogo;
    private String brandName;
    private JobOrderStatus orderStatus;
    private Integer jobOrderStatus;
    private BigDecimal paidFromWallet;
    private BigDecimal paidFromCod;
    private BigDecimal cashBackAmount;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @JsonIgnore
    public Integer getJobOrderStatus() {
        return jobOrderStatus;
    }

    public void setJobOrderStatus(Integer jobOrderStatus) {
        this.jobOrderStatus = jobOrderStatus;
        this.setOrderStatus(JobOrderStatus.fromInt(jobOrderStatus));
    }

    public BigDecimal getPaidFromWallet() {
        return paidFromWallet;
    }

    public void setPaidFromWallet(BigDecimal paidFromWallet) {
        this.paidFromWallet = paidFromWallet;
    }

    public BigDecimal getPaidFromCod() {
        return paidFromCod;
    }

    public void setPaidFromCod(BigDecimal paidFromCod) {
        this.paidFromCod = paidFromCod;
    }

    public BigDecimal getCashBackAmount() {
        return cashBackAmount;
    }

    public void setCashBackAmount(BigDecimal cashBackAmount) {
        this.cashBackAmount = cashBackAmount;
    }
}
