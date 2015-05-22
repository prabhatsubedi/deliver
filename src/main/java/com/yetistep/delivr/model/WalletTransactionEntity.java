package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.AccountType;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 3/30/15
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="WalletTransactionEntity")
@Table(name = "wallet_transaction")
public class WalletTransactionEntity {
    private Integer id;
    private Timestamp transactionDate;
    private BigDecimal transactionAmount;
    private String remarks;
    private AccountType accountType;
    private String signature;
    private BigDecimal availableWalletAmount;
    private PaymentMode paymentMode;
    private CustomerEntity customer;
    private OrderEntity order;
    /* Transient variable to represent if transaction is fine or not. i.e. True for fine */
    private Boolean flag;
    private Integer orderId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "transaction_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Column(name = "transaction_amount", precision =  19, scale = 2)
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Column(name = "remarks")
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Column(name = "account_type")
    @Enumerated(EnumType.ORDINAL)
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Column(name = "signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Column(name = "available_amount", precision =  19, scale = 2)
    public BigDecimal getAvailableWalletAmount() {
        return availableWalletAmount;
    }

    public void setAvailableWalletAmount(BigDecimal availableWalletAmount) {
        this.availableWalletAmount = availableWalletAmount;
    }

    @Column(name = "payment_mode")
    @Enumerated(EnumType.ORDINAL)
    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @Transient
    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Transient
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "WalletTransactionEntity{" +
                "id=" + id +
                ", transactionDate=" + transactionDate +
                ", transactionAmount=" + transactionAmount +
                ", remarks='" + remarks + '\'' +
                ", accountType=" + accountType +
                ", signature='" + signature + '\'' +
                ", availableWalletAmount=" + availableWalletAmount +
                ", paymentMode=" + paymentMode +
                ", customer=" + customer +
                ", order=" + order +
                ", flag=" + flag +
                ", orderId=" + orderId +
                '}';
    }
}
