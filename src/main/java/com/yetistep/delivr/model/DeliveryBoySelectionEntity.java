package com.yetistep.delivr.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/19/14
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name = "DeliveryBoySelectionEntity")
@Table(name = "dboy_selections")
public class DeliveryBoySelectionEntity {
    private Integer id;
    private BigDecimal storeToCustomerDistance;
    private BigDecimal distanceToStore;
    private Integer timeRequired;
    private Integer totalTimeRequired;
    private Boolean accepted;
    private DeliveryBoyEntity deliveryBoy;
    private OrderEntity order;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "customer_chargeable_distance")
    public BigDecimal getStoreToCustomerDistance() {
        return storeToCustomerDistance;
    }

    public void setStoreToCustomerDistance(BigDecimal storeToCustomerDistance) {
        this.storeToCustomerDistance = storeToCustomerDistance;
    }

    @Column(name = "system_chargeable_distance")
    public BigDecimal getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(BigDecimal distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

    @Column(name = "time_required")
    public Integer getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(Integer timeRequired) {
        this.timeRequired = timeRequired;
    }

    @Column(name = "total_time_required")
    public Integer getTotalTimeRequired() {
        return totalTimeRequired;
    }

    public void setTotalTimeRequired(Integer totalTimeRequired) {
        this.totalTimeRequired = totalTimeRequired;
    }

    @Column(name = "accepted", nullable = false, columnDefinition = "TINYINT(1)")
    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @ManyToOne
    @JoinColumn(name = "dboy_id")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "DeliveryBoySelectionEntity{" +
                "id=" + id +
                ", storeToCustomerDistance=" + storeToCustomerDistance +
                ", distanceToStore=" + distanceToStore +
                ", timeRequired=" + timeRequired +
                ", deliveryBoy=" + deliveryBoy.getId() +
                ", order=" + order +
                ", isAccepted=" + accepted +
                '}';
    }
}
