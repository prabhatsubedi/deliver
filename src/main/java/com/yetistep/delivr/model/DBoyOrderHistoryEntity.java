package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.DeliveryStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/15/14
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "DBoyOrderHistory")
@Table(name = "dboy_order_history")
public class DBoyOrderHistoryEntity implements Serializable {
    private Integer id;
    private BigDecimal distanceTravelled;
    private DeliveryStatus deliveryStatus;
    private BigDecimal amountEarned;
    private Timestamp orderAcceptedAt;
    private Timestamp jobStartedAt;
    private Timestamp reachedStoreAt;
    private Timestamp orderCompletedAt;
    private String startLatitude;
    private String startLongitude;
    private String endLatitude;
    private String endLongitude;
    //commission paid to the shopper
    private Boolean dBoyPaid;
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

    @Column(name = "distance_travelled")
    public BigDecimal getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(BigDecimal distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    @Column(name = "delivery_status")
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Column(name = "amount_earned", precision = 19, scale = 2)
    public BigDecimal getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(BigDecimal amountEarned) {
        this.amountEarned = amountEarned;
    }

    @Column(name="order_accepted_at", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getOrderAcceptedAt() {
        return orderAcceptedAt;
    }

    public void setOrderAcceptedAt(Timestamp orderAcceptedAt) {
        this.orderAcceptedAt = orderAcceptedAt;
    }

    @Column(name="job_started_at", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getJobStartedAt() {
        return jobStartedAt;
    }

    public void setJobStartedAt(Timestamp jobStartedAt) {
        this.jobStartedAt = jobStartedAt;
    }

    @Column(name="reached_stored_at", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getReachedStoreAt() {
        return reachedStoreAt;
    }

    public void setReachedStoreAt(Timestamp reachedStoreAt) {
        this.reachedStoreAt = reachedStoreAt;
    }

    @Column(name="completed_at", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getOrderCompletedAt() {
        return orderCompletedAt;
    }

    public void setOrderCompletedAt(Timestamp orderCompletedAt) {
        this.orderCompletedAt = orderCompletedAt;
    }

    @Column(name="start_latitude")
    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    @Column(name="start_longitude")
    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    @Column(name="end_latitude")
    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    @Column(name="end_longitude")
    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    @Column(name = "dboy_paid")
    public Boolean getdBoyPaid() {
        return dBoyPaid;
    }

    public void setdBoyPaid(Boolean dBoyPaid) {
        this.dBoyPaid = dBoyPaid;
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
}
