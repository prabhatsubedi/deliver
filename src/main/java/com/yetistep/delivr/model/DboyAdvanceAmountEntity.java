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
@Entity(name="DboyAdvanceAmountEntity")
@Table(name = "dboy_advance_amounts")
public class DBoyAdvanceAmountEntity implements Serializable {

    private Integer id;
    private Timestamp date;
    private DeliveryBoyEntity deliveryBoy;
    private BigDecimal amountAdvance;

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
    @Column(name = "date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
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
}
