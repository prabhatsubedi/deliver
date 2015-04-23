package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/26/15
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ReceiptEntity")
@Table(name = "receipts")
public class ReceiptEntity {

    private Integer id;
    private Date generatedDate;
    private String path;
    private BigDecimal receiptAmount;
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
    @Column(name = "generated_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
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

    @Column(name = "receipt_amount")
    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE } , fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }


}
