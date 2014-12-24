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
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "DboySubmittedAmountEntity")
@Table(name = "dboy_submitted_amounts")
public class DBoySubmittedAmountEntity {

    private Integer id;
    private Timestamp submissionDate;
    private DeliveryBoyEntity deliveryBoy;
    private BigDecimal amountReceived;
    private Timestamp ackDate;

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
    @Column(name = "submission_date", columnDefinition="TIMESTAMP")
    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
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

    @Column(name = "amount_received")
    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "ack_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getAckDate() {
        return ackDate;
    }

    public void setAckDate(Timestamp ackDate) {
        this.ackDate = ackDate;
    }
}
