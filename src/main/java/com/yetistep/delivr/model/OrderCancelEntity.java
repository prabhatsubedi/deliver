package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/8/15
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "OrderCancelEntity")
@Table(name = "order_cancel")
public class OrderCancelEntity {
    private Integer id;
    private String reason;
    private JobOrderStatus jobOrderStatus;
    private Timestamp cancelledDate;
    private UserEntity user;
    private OrderEntity order;
    private ReasonDetailsEntity reasonDetails;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "order_status")
    public JobOrderStatus getJobOrderStatus() {
        return jobOrderStatus;
    }

    public void setJobOrderStatus(JobOrderStatus jobOrderStatus) {
        this.jobOrderStatus = jobOrderStatus;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "cancelled_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Timestamp cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    @ManyToOne
    @JoinColumn(name = "cancelled_by")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @OneToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @ManyToOne
    @JoinColumn(name = "reason_detail_id")
    public ReasonDetailsEntity getReasonDetails() {
        return reasonDetails;
    }

    public void setReasonDetails(ReasonDetailsEntity reasonDetails) {
        this.reasonDetails = reasonDetails;
    }

}
