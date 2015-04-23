package com.yetistep.delivr.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/21/15
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name="PaymentGatewayInfoEntity")
@Table(name = "payment_gateway_info")
public class PaymentGatewayInfoEntity {
    private Integer id;
    private BigDecimal amount;
    private BigDecimal inrAmount;
    private String currencyCode;
    private String transactionReference;
    private Boolean flag;
    private String responseCode;
    private CustomerEntity customer;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "amount", precision = 19, scale = 2)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name= "inr_amount", precision = 19, scale = 2)
    public BigDecimal getInrAmount() {
        return inrAmount;
    }

    public void setInrAmount(BigDecimal inrAmount) {
        this.inrAmount = inrAmount;
    }

    @Column(name = "currency_code")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "transaction_reference", unique = true)
    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    @Column(name = "flag", columnDefinition = "TINYINT(1)")
    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Column(name = "response_code")
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }
}
