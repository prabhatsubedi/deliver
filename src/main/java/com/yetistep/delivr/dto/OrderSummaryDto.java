package com.yetistep.delivr.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.ItemsOrderEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.util.JsonDateSerializer;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private JobOrderStatus orderStatus;
    private String orderVerificationCode;
    private Timestamp orderDate;
    private List<String> attachments;
    private AccountSummary accountSummary;
    private List<ItemsOrderEntity> itemOrders;
    private StoreEntity store;
    private DeliveryBoyEntity deliveryBoy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderVerificationCode() {
        return orderVerificationCode;
    }

    public void setOrderVerificationCode(String orderVerificationCode) {
        this.orderVerificationCode = orderVerificationCode;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }


    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public AccountSummary getAccountSummary() {
        return accountSummary;
    }

    public void setAccountSummary(AccountSummary accountSummary) {
        this.accountSummary = accountSummary;
    }

    public List<ItemsOrderEntity> getItemOrders() {
        return itemOrders;
    }

    public void setItemOrders(List<ItemsOrderEntity> itemOrders) {
        this.itemOrders = itemOrders;
    }

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    public class AccountSummary{
        private BigDecimal subTotal;
        private BigDecimal vatAndServiceCharge;
        private BigDecimal itemServiceCharge;
        private BigDecimal itemVatCharge;
        private BigDecimal serviceFee;
        private BigDecimal deliveryFee;
        private BigDecimal totalDiscount;
        private BigDecimal estimatedTotal;
        private Boolean partnerShipStatus;
        private BigDecimal paidFromWallet;
        private BigDecimal paidFromCOD;
        private PaymentMode paymentMode;
        private String currency;

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

        public BigDecimal getItemServiceCharge() {
            return itemServiceCharge;
        }

        public void setItemServiceCharge(BigDecimal itemServiceCharge) {
            this.itemServiceCharge = itemServiceCharge;
        }

        public BigDecimal getItemVatCharge() {
            return itemVatCharge;
        }

        public void setItemVatCharge(BigDecimal itemVatCharge) {
            this.itemVatCharge = itemVatCharge;
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

        public BigDecimal getPaidFromWallet() {
            return paidFromWallet;
        }

        public void setPaidFromWallet(BigDecimal paidFromWallet) {
            this.paidFromWallet = paidFromWallet;
        }

        public BigDecimal getPaidFromCOD() {
            return paidFromCOD;
        }

        public void setPaidFromCOD(BigDecimal paidFromCOD) {
            this.paidFromCOD = paidFromCOD;
        }

        public PaymentMode getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(PaymentMode paymentMode) {
            this.paymentMode = paymentMode;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
