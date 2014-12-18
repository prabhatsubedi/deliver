package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.DeliveryStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    private Date timeTaken;
    private Integer rating;
    private String ratingComments;
    private DeliveryBoyEntity deliveryBoy;
    private OrderEntity order;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
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

    @Column(name = "amount_earned", precision = 4, scale = 2)
    public BigDecimal getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(BigDecimal amountEarned) {
        this.amountEarned = amountEarned;
    }

    @Temporal(TemporalType.TIME)
    public Date getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Date timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Column(name="rating")
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Column(name="rating_comments")
    public String getRatingComments() {
        return ratingComments;
    }

    public void setRatingComments(String ratingComments) {
        this.ratingComments = ratingComments;
    }

    @ManyToOne
    @JoinColumn(name = "dboy_id")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @OneToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
