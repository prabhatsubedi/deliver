package com.yetistep.delivr.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
//@Entity(name = "CourierTransactionEntity")
//@Table(name = "courier_transaction")
public class CourierTransactionEntity {
    private Integer id;
    //FIXME
    private Integer orderId;
    private Integer customerId;
    private Integer deliveryBoyId;
    //Until Here......
    private BigDecimal orderTotal;
    private BigDecimal commissionPct;
    private BigDecimal storeToCustomerDistance;
    private BigDecimal courierToStoreDistance;
    private BigDecimal serviceFeePct;
    private BigDecimal additionalDeliveryAmt;
    private BigDecimal customerDiscount;
    private Integer surgeFactor;
    private BigDecimal deliveryCostWithoutAdditionalDvAmt;
    private BigDecimal serviceFeeAmt;
    private BigDecimal deliveryChargedBeforeDiscount;
    private BigDecimal customerBalanceBeforeDiscount;
    private BigDecimal deliveryChargedAfterDiscount;
    private BigDecimal customerBalanceAfterDiscount;
    private BigDecimal customerPays;
    private BigDecimal paidToCourier;
    private BigDecimal profit;
    private Integer timeTaken;

    //@Id
    //@GeneratedValue
    //@Column(name="id")
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(Integer deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    @Column(name ="order_total")
    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getCommissionPct() {
        return commissionPct;
    }

    public void setCommissionPct(BigDecimal commissionPct) {
        this.commissionPct = commissionPct;
    }

    public BigDecimal getStoreToCustomerDistance() {
        return storeToCustomerDistance;
    }

    public void setStoreToCustomerDistance(BigDecimal storeToCustomerDistance) {
        this.storeToCustomerDistance = storeToCustomerDistance;
    }

    public BigDecimal getCourierToStoreDistance() {
        return courierToStoreDistance;
    }

    public void setCourierToStoreDistance(BigDecimal courierToStoreDistance) {
        this.courierToStoreDistance = courierToStoreDistance;
    }

    public BigDecimal getServiceFeePct() {
        return serviceFeePct;
    }

    public void setServiceFeePct(BigDecimal serviceFeePct) {
        this.serviceFeePct = serviceFeePct;
    }

    public BigDecimal getAdditionalDeliveryAmt() {
        return additionalDeliveryAmt;
    }

    public void setAdditionalDeliveryAmt(BigDecimal additionalDeliveryAmt) {
        this.additionalDeliveryAmt = additionalDeliveryAmt;
    }

    public BigDecimal getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(BigDecimal customerDiscount) {
        this.customerDiscount = customerDiscount;
    }

    public Integer getSurgeFactor() {
        return surgeFactor;
    }

    public void setSurgeFactor(Integer surgeFactor) {
        this.surgeFactor = surgeFactor;
    }

    public BigDecimal getDeliveryCostWithoutAdditionalDvAmt() {
        return deliveryCostWithoutAdditionalDvAmt;
    }

    public void setDeliveryCostWithoutAdditionalDvAmt(BigDecimal deliveryCostWithoutAdditionalDvAmt) {
        this.deliveryCostWithoutAdditionalDvAmt = deliveryCostWithoutAdditionalDvAmt;
    }

    public BigDecimal getServiceFeeAmt() {
        return serviceFeeAmt;
    }

    public void setServiceFeeAmt(BigDecimal serviceFeeAmt) {
        this.serviceFeeAmt = serviceFeeAmt;
    }

    public BigDecimal getDeliveryChargedBeforeDiscount() {
        return deliveryChargedBeforeDiscount;
    }

    public void setDeliveryChargedBeforeDiscount(BigDecimal deliveryChargedBeforeDiscount) {
        this.deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount;
    }

    public BigDecimal getCustomerBalanceBeforeDiscount() {
        return customerBalanceBeforeDiscount;
    }

    public void setCustomerBalanceBeforeDiscount(BigDecimal customerBalanceBeforeDiscount) {
        this.customerBalanceBeforeDiscount = customerBalanceBeforeDiscount;
    }

    public BigDecimal getDeliveryChargedAfterDiscount() {
        return deliveryChargedAfterDiscount;
    }

    public void setDeliveryChargedAfterDiscount(BigDecimal deliveryChargedAfterDiscount) {
        this.deliveryChargedAfterDiscount = deliveryChargedAfterDiscount;
    }

    public BigDecimal getCustomerBalanceAfterDiscount() {
        return customerBalanceAfterDiscount;
    }

    public void setCustomerBalanceAfterDiscount(BigDecimal customerBalanceAfterDiscount) {
        this.customerBalanceAfterDiscount = customerBalanceAfterDiscount;
    }

    public BigDecimal getCustomerPays() {
        return customerPays;
    }

    public void setCustomerPays(BigDecimal customerPays) {
        this.customerPays = customerPays;
    }

    public BigDecimal getPaidToCourier() {
        return paidToCourier;
    }

    public void setPaidToCourier(BigDecimal paidToCourier) {
        this.paidToCourier = paidToCourier;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }
}
