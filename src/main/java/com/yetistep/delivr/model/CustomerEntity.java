package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="CustomerEntity")
@Table(name="customers")
public class CustomerEntity implements Serializable {

    private Integer id;
    private UserEntity user;
    private Long facebookId;
    private Long referredBy;
    private String fbToken;
    private String profileUrl;
    private Boolean allowShare;
    private List<OrderEntity> order;
    private Integer totalOrderPlaced;
    private Integer totalOrderDelivered;
    private BigDecimal averageRating;
    private Integer friendsInvitationCount;
    private String referenceUrl;
    private Integer referredFriendsCount;
    private BigDecimal rewardsEarned;
    private String creditCardToken;
    private String creditCardId;
    private String latitude;
    private String longitude;
    private List<CartEntity> carts;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(cascade = { CascadeType.ALL})
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "customer")
    public List<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(List<OrderEntity> order) {
        this.order = order;
    }

    @Column(name = "total_order_placed")
    public Integer getTotalOrderPlaced() {
        return totalOrderPlaced;
    }

    public void setTotalOrderPlaced(Integer totalOrderPlaced) {
        this.totalOrderPlaced = totalOrderPlaced;
    }

    @Column(name = "total_order_delivered")
    public Integer getTotalOrderDelivered() {
        return totalOrderDelivered;
    }

    public void setTotalOrderDelivered(Integer totalOrderDelivered) {
        this.totalOrderDelivered = totalOrderDelivered;
    }

    @Column(name = "average_rating", precision = 4, scale = 2)
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    @Column(name = "friends_invitation_count")
    public Integer getFriendsInvitationCount() {
        return friendsInvitationCount;
    }

    public void setFriendsInvitationCount(Integer friendsInvitationCount) {
        this.friendsInvitationCount = friendsInvitationCount;
    }

    @Column(name = "reference_url")
    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    @Column(name = "referred_friends_count")
    public Integer getReferredFriendsCount() {
        return referredFriendsCount;
    }

    public void setReferredFriendsCount(Integer referredFriendsCount) {
        this.referredFriendsCount = referredFriendsCount;
    }

    @Column(name = "rewards_earned")
    public BigDecimal getRewardsEarned() {
        return rewardsEarned;
    }

    public void setRewardsEarned(BigDecimal rewardsEarned) {
        this.rewardsEarned = rewardsEarned;
    }

    @Column(name = "credit_card_token")
    public String getCreditCardToken() {
        return creditCardToken;
    }

    public void setCreditCardToken(String creditCardToken) {
        this.creditCardToken = creditCardToken;
    }

    @Column(name = "credit_card_id")
    public String getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }
    @Column(name = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Column (name = "facebook_id", unique = true)
    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    @Column (name = "referred_by")
    public Long getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(Long referredBy) {
        this.referredBy = referredBy;
    }

    @Column(name ="fb_token")
    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    @Column(name = "profile_url")
    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Column(name = "allow_share", columnDefinition = "TINYINT(1)")
    public Boolean getAllowShare() {
        return allowShare;
    }

    public void setAllowShare(Boolean allowShare) {
        this.allowShare = allowShare;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<CartEntity> getCarts() {
        return carts;
    }

    public void setCarts(List<CartEntity> carts) {
        this.carts = carts;
    }
}
