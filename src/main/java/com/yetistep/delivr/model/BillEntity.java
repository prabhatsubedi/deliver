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
    private BigDecimal amount;
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





    @OneToOne(cascade = CascadeType.PERSIST)
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @OneToOne(cascade = CascadeType.PERSIST)
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }
}
