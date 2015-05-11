package com.yetistep.delivr.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/5/15
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "dboy_payment")
public class DBoyPaymentEntity {

    private Integer id;
    private Date generatedDate;
    private String path;
    private BigDecimal payableAmount;
    private Date fromDate;
    private Date toDate;
    private Date paidDate;
    private Boolean dBoyPaid;
    private List<OrderEntity> orders;
    private DeliveryBoyEntity deliveryBoy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "generated_date")
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

    @Column(name = "payable_amount")
    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
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

    @Column(name = "paid_date")
    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    @Column(name = "dboy_paid")
    public Boolean getdBoyPaid() {
        return dBoyPaid;
    }

    public void setdBoyPaid(Boolean dBoyPaid) {
        this.dBoyPaid = dBoyPaid;
    }

    @OneToMany(mappedBy = "dBoyPayment")
    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    @ManyToOne
    @JoinColumn(name = "dboy_id")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

}
