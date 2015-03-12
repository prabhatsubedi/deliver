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
 * Date: 2/20/15
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "InvoiceEntity")
@Table(name = "invoices")
public class InvoiceEntity {

    private Integer id;
    private Date generatedDate;
    private String path;
    private InvoiceStatus invoiceStatus;
    private BigDecimal amount;
    private Date fromDate;
    private Date toDate;
    private Date paidDate;
    private Boolean invoicePaid;
    private List<OrderEntity> orders;
    private MerchantEntity merchant;
    private  StoreEntity store;


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

    @Column(name = "invoice_amount")
    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    @Column(name = "paid_amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "paid_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    @Column(name = "from_date")
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Column(name = "to_date")
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Column(name = "invoice_paid")
    public Boolean getInvoicePaid() {
        return invoicePaid;
    }

    public void setInvoicePaid(Boolean invoicePaid) {
        this.invoicePaid = invoicePaid;
    }

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.PERSIST)
    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }
}
