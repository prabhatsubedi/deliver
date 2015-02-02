package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yetistep.delivr.enums.RatingReason;
import com.yetistep.delivr.enums.RatingType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "RatingEntity")
@Table(name = "ratings")
public class RatingEntity {
    private Integer id;
    /* Rating of customer */
    private RatingType customerRating;
    /* Rating of delivery boy */
    private RatingType deliveryBoyRating;
    /* Comment of customer for delivery boy */
    private String customerComment;
    /* Comment of delivery boy for customer */
    private String deliveryBoyComment;
    private OrderEntity order;
    private List<RatingReason> ratingIssues;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "customer_rating")
    @Type(type = "com.yetistep.delivr.enums.RatingTypeCustom")
    public RatingType getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(RatingType customerRating) {
        this.customerRating = customerRating;
    }

    @Column(name = "dboy_rating")
    @Type(type = "com.yetistep.delivr.enums.RatingTypeCustom")
    public RatingType getDeliveryBoyRating() {
        return deliveryBoyRating;
    }

    public void setDeliveryBoyRating(RatingType deliveryBoyRating) {
        this.deliveryBoyRating = deliveryBoyRating;
    }

    @Column(name = "customer_comments")
    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    @Column(name = "dboy_comments")
    public String getDeliveryBoyComment() {
        return deliveryBoyComment;
    }

    public void setDeliveryBoyComment(String deliveryBoyComment) {
        this.deliveryBoyComment = deliveryBoyComment;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @ElementCollection
    @CollectionTable(name = "rating_issues", joinColumns = @JoinColumn(name = "rating_id"))
    @Column(name = "issues")
    public List<RatingReason> getRatingIssues() {
        return ratingIssues;
    }

    public void setRatingIssues(List<RatingReason> ratingIssues) {
        this.ratingIssues = ratingIssues;
    }
}
