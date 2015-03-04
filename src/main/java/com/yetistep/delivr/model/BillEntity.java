package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.InvoiceStatus;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/26/15
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="BillEntity")
@Table(name = "bills")
public class BillEntity {

    private Integer id;
    private Date generatedDate;
    private String path;
    private BigDecimal vat;
    private BigDecimal deliveryCharge;
    private BigDecimal systemServiceCharge;
    private BigDecimal billAmount;
    private OrderEntity order;
    private CustomerEntity customer;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "generated_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Column(name = "vat")
    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    @Column(name = "delivery_charge")
    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    @Column(name = "service_charge")
    public BigDecimal getSystemServiceCharge() {
        return systemServiceCharge;
    }

    public void setSystemServiceCharge(BigDecimal systemServiceCharge) {
        this.systemServiceCharge = systemServiceCharge;
    }

    @Column(name = "bill_amount")
    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }
}
