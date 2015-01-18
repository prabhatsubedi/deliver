package com.yetistep.delivr.dto;

import com.yetistep.delivr.model.ItemsOrderEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/18/15
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderSummaryDto {
    private Integer id;
    private BigDecimal subTotal;
    private BigDecimal vatAndServiceCharge;
    private BigDecimal serviceFee;
    private BigDecimal deliveryFee;
    private BigDecimal totalDiscount;
    private BigDecimal estimatedTotal;
    private Boolean partnerShipStatus;
    private List<ItemsOrderEntity> itemOrders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getVatAndServiceCharge() {
        return vatAndServiceCharge;
    }

    public void setVatAndServiceCharge(BigDecimal vatAndServiceCharge) {
        this.vatAndServiceCharge = vatAndServiceCharge;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getEstimatedTotal() {
        return estimatedTotal;
    }

    public void setEstimatedTotal(BigDecimal estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public Boolean getPartnerShipStatus() {
        return partnerShipStatus;
    }

    public void setPartnerShipStatus(Boolean partnerShipStatus) {
        this.partnerShipStatus = partnerShipStatus;
    }

    public List<ItemsOrderEntity> getItemOrders() {
        return itemOrders;
    }

    public void setItemOrders(List<ItemsOrderEntity> itemOrders) {
        this.itemOrders = itemOrders;
    }
}
