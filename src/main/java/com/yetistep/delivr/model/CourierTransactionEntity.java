package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "CourierTransactionEntity")
@Table(name = "courier_transaction")
public class CourierTransactionEntity {
    private Integer id;
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
    private OrderEntity order;

    @Id
    @GeneratedValue
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name ="order_total", precision = 16, scale = 2)
    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    @Column(name ="commission_pct", precision = 4, scale = 2)
    public BigDecimal getCommissionPct() {
        return commissionPct;
    }

    public void setCommissionPct(BigDecimal commissionPct) {
        this.commissionPct = commissionPct;
    }

    @Column(name ="store_to_customer_distance", precision = 5, scale = 2)
    public BigDecimal getStoreToCustomerDistance() {
        return storeToCustomerDistance;
    }

    public void setStoreToCustomerDistance(BigDecimal storeToCustomerDistance) {
        this.storeToCustomerDistance = storeToCustomerDistance;
    }

    @Column(name ="courier_to_store_distance", precision = 5, scale = 2)
    public BigDecimal getCourierToStoreDistance() {
        return courierToStoreDistance;
    }

    public void setCourierToStoreDistance(BigDecimal courierToStoreDistance) {
        this.courierToStoreDistance = courierToStoreDistance;
    }

    @Column(name ="service_fee_pct", precision = 4, scale = 2)
    public BigDecimal getServiceFeePct() {
        return serviceFeePct;
    }

    public void setServiceFeePct(BigDecimal serviceFeePct) {
        this.serviceFeePct = serviceFeePct;
    }

    @Column(name ="additional_delivery_amt", precision = 16, scale = 2)
    public BigDecimal getAdditionalDeliveryAmt() {
        return additionalDeliveryAmt;
    }

    public void setAdditionalDeliveryAmt(BigDecimal additionalDeliveryAmt) {
        this.additionalDeliveryAmt = additionalDeliveryAmt;
    }

    @Column(name ="customer_discount", precision = 16, scale = 2)
    public BigDecimal getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(BigDecimal customerDiscount) {
        this.customerDiscount = customerDiscount;
    }

    @Column(name ="surge_factor")
    public Integer getSurgeFactor() {
        return surgeFactor;
    }

    public void setSurgeFactor(Integer surgeFactor) {
        this.surgeFactor = surgeFactor;
    }

    @Column(name ="delivery_cost_without_additional_dv_amt", precision = 16, scale = 2)
    public BigDecimal getDeliveryCostWithoutAdditionalDvAmt() {
        return deliveryCostWithoutAdditionalDvAmt;
    }

    public void setDeliveryCostWithoutAdditionalDvAmt(BigDecimal deliveryCostWithoutAdditionalDvAmt) {
        this.deliveryCostWithoutAdditionalDvAmt = deliveryCostWithoutAdditionalDvAmt;
    }

    @Column(name ="service_fee_amt", precision = 16, scale = 2)
    public BigDecimal getServiceFeeAmt() {
        return serviceFeeAmt;
    }

    public void setServiceFeeAmt(BigDecimal serviceFeeAmt) {
        this.serviceFeeAmt = serviceFeeAmt;
    }

    @Column(name ="delivery_charge_before_discount", precision = 16, scale = 2)
    public BigDecimal getDeliveryChargedBeforeDiscount() {
        return deliveryChargedBeforeDiscount;
    }

    public void setDeliveryChargedBeforeDiscount(BigDecimal deliveryChargedBeforeDiscount) {
        this.deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount;
    }

    @Column(name ="customer_balance_before_discount", precision = 16, scale = 2)
    public BigDecimal getCustomerBalanceBeforeDiscount() {
        return customerBalanceBeforeDiscount;
    }

    public void setCustomerBalanceBeforeDiscount(BigDecimal customerBalanceBeforeDiscount) {
        this.customerBalanceBeforeDiscount = customerBalanceBeforeDiscount;
    }

    @Column(name ="delivery_charge_after_discount", precision = 16, scale = 2)
    public BigDecimal getDeliveryChargedAfterDiscount() {
        return deliveryChargedAfterDiscount;
    }

    public void setDeliveryChargedAfterDiscount(BigDecimal deliveryChargedAfterDiscount) {
        this.deliveryChargedAfterDiscount = deliveryChargedAfterDiscount;
    }

    @Column(name ="customer_balance_after_discount", precision = 16, scale = 2)
    public BigDecimal getCustomerBalanceAfterDiscount() {
        return customerBalanceAfterDiscount;
    }

    public void setCustomerBalanceAfterDiscount(BigDecimal customerBalanceAfterDiscount) {
        this.customerBalanceAfterDiscount = customerBalanceAfterDiscount;
    }



    @Column(name ="customer_pays", precision = 16, scale = 2)
    public BigDecimal getCustomerPays() {
        return customerPays;
    }

    public void setCustomerPays(BigDecimal customerPays) {
        this.customerPays = customerPays;
    }

    @Column(name ="paid_to_courier", precision = 16, scale = 2)
    public BigDecimal getPaidToCourier() {
        return paidToCourier;
    }

    public void setPaidToCourier(BigDecimal paidToCourier) {
        this.paidToCourier = paidToCourier;
    }

    @Column(name ="profit", precision = 16, scale = 2)
    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    @JsonIgnore
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
