package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/2/15
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ReasonDetails")
@Table(name = "reason_details")
public class ReasonDetails {
    private Integer id;
    private String cancelReason;
    private Boolean status;
    private List<OrderCancelEntity> orderCancelEntities;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="cancel_reason")
    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @Column(name="status", columnDefinition = "TINYINT(1)")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "reasonDetails")
    public List<OrderCancelEntity> getOrderCancelEntities() {
        return orderCancelEntities;
    }

    public void setOrderCancelEntities(List<OrderCancelEntity> orderCancelEntities) {
        this.orderCancelEntities = orderCancelEntities;
    }
}
