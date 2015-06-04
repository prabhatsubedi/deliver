package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/24/14
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="DBoyAdvanceAmountEntity")
@Table(name = "dboy_advance_amounts")
public class DBoyAdvanceAmountEntity implements Serializable {

    private Integer id;
    private Timestamp advanceDate;
    private BigDecimal amountAdvance;
    private String type;
    private String accountantNote;
    private DeliveryBoyEntity deliveryBoy;
    private OrderEntity order;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "advance_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getAdvanceDate() {
        return advanceDate;
    }

    public void setAdvanceDate(Timestamp advanceDate) {
        this.advanceDate = advanceDate;
    }

    @ManyToOne
    @JoinColumn(name = "dboy_id")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @Column(name = "advance_amount")
    public BigDecimal getAmountAdvance() {
        return amountAdvance;
    }

    public void setAmountAdvance(BigDecimal amountAdvance) {
        this.amountAdvance = amountAdvance;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "accountant_note")
    public String getAccountantNote() {
        return accountantNote;
    }

    public void setAccountantNote(String accountantNote) {
        this.accountantNote = accountantNote;
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
