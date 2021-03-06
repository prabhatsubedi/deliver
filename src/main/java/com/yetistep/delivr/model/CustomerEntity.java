package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yetistep.delivr.enums.CustomerType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
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
    private Integer invitedFriendsCount;
    private BigDecimal rewardsEarned;
    private String creditCardToken;
    private String creditCardId;
    private String latitude;
    private String longitude;
    private Boolean isDefault;
    private BigDecimal walletAmount;
    private BigDecimal shortFallAmount;
    private CustomerType customerType;
    private List<CartEntity> carts;
    private String currency;//Transient Variable
    private List<BillEntity> bill;
    private List<ReceiptEntity> receipt;
    private List<WalletTransactionEntity> walletTransactions;
    private List<PaymentGatewayInfoEntity> paymentGatewayInfo;

    private BigDecimal rmBonusAmount; //remaining bonus amount
    private BigDecimal rmCashBackAmount; //remaining cash back amount
    private BigDecimal rmFundTransferAmount; //remaining fund transfer amount

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonProperty
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonProperty
    public List<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(List<OrderEntity> order) {
        this.order = order;
    }

    @Column(name = "total_order_placed")
    @JsonProperty
    public Integer getTotalOrderPlaced() {
        return totalOrderPlaced;
    }

    public void setTotalOrderPlaced(Integer totalOrderPlaced) {
        this.totalOrderPlaced = totalOrderPlaced;
    }

    @Column(name = "total_order_delivered")
    @JsonProperty
    public Integer getTotalOrderDelivered() {
        return totalOrderDelivered;
    }

    public void setTotalOrderDelivered(Integer totalOrderDelivered) {
        this.totalOrderDelivered = totalOrderDelivered;
    }

    @Column(name = "average_rating", precision = 4, scale = 2)
    @JsonProperty
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    @Column(name = "friends_invitation_count")
    @JsonProperty
    public Integer getFriendsInvitationCount() {
        return friendsInvitationCount;
    }

    public void setFriendsInvitationCount(Integer friendsInvitationCount) {
        this.friendsInvitationCount = friendsInvitationCount;
    }

    @Column(name = "reference_url")
    @JsonProperty
    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    @Column(name = "referred_friends_count")
    @JsonProperty
    public Integer getReferredFriendsCount() {
        return referredFriendsCount;
    }

    public void setReferredFriendsCount(Integer referredFriendsCount) {
        this.referredFriendsCount = referredFriendsCount;
    }

    @Column(name = "invited_friends_count")
    @JsonProperty
    public Integer getInvitedFriendsCount() {
        return invitedFriendsCount;
    }

    public void setInvitedFriendsCount(Integer invitedFriendsCount) {
        this.invitedFriendsCount = invitedFriendsCount;
    }

    @Column(name = "rewards_earned")
    @JsonProperty
    public BigDecimal getRewardsEarned() {
        return rewardsEarned;
    }

    public void setRewardsEarned(BigDecimal rewardsEarned) {
        this.rewardsEarned = rewardsEarned;
    }


    @Column(name = "credit_card_token")
    @JsonProperty
    public String getCreditCardToken() {
        return creditCardToken;
    }

    public void setCreditCardToken(String creditCardToken) {
        this.creditCardToken = creditCardToken;
    }

    @Column(name = "credit_card_id")
    @JsonProperty
    public String getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }
    @Column(name = "latitude")
    @JsonProperty
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    @JsonProperty
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Column (name = "is_default", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    @JsonProperty
    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    @Column (name = "facebook_id", unique = true)
    @JsonProperty
    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    @Column (name = "referred_by")
    @JsonProperty
    public Long getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(Long referredBy) {
        this.referredBy = referredBy;
    }

    @Column(name ="fb_token", columnDefinition="LONGTEXT NULL DEFAULT NULL")
    @JsonProperty
    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    @Column(name = "profile_url")
    @JsonProperty
    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Column(name = "allow_share", columnDefinition = "TINYINT(1)")
    @JsonProperty
    public Boolean getAllowShare() {
        return allowShare;
    }

    public void setAllowShare(Boolean allowShare) {
        this.allowShare = allowShare;
    }

    @JsonProperty
    @Column(name = "wallet_amount", precision =  19, scale = 2)
    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    @JsonProperty
    @Column(name = "shortfall_amount", precision =  19, scale = 2)
    public BigDecimal getShortFallAmount() {
        return shortFallAmount;
    }

    public void setShortFallAmount(BigDecimal shortFallAmount) {
        this.shortFallAmount = shortFallAmount;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST)
    @JsonProperty
    public List<CartEntity> getCarts() {
        return carts;
    }

    public void setCarts(List<CartEntity> carts) {
        this.carts = carts;
    }

    @Transient
    @JsonProperty
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST)
    public List<BillEntity> getBill() {
        return bill;
    }

    public void setBill(List<BillEntity> bill) {
        this.bill = bill;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST)
    public List<ReceiptEntity> getReceipt() {
        return receipt;
    }

    public void setReceipt(List<ReceiptEntity> receipt) {
        this.receipt = receipt;
    }

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<WalletTransactionEntity> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransactionEntity> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    public List<PaymentGatewayInfoEntity> getPaymentGatewayInfo() {
        return paymentGatewayInfo;
    }

    public void setPaymentGatewayInfo(List<PaymentGatewayInfoEntity> paymentGatewayInfo) {
        this.paymentGatewayInfo = paymentGatewayInfo;
    }

    @Column(name = "customer_type")
    @Enumerated(EnumType.ORDINAL)
    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    @Column(name = "rm_bonus_amount", columnDefinition = "Decimal(19,2) default '0.00'")
    @JsonProperty
    public BigDecimal getRmBonusAmount() {
        return rmBonusAmount;
    }

    public void setRmBonusAmount(BigDecimal rmBonusAmount) {
        this.rmBonusAmount = rmBonusAmount;
    }

    @Column(name = "rm_cash_back_amount", columnDefinition = "Decimal(19,2) default '0.00'")
    @JsonProperty
    public BigDecimal getRmCashBackAmount() {
        return rmCashBackAmount;
    }

    public void setRmCashBackAmount(BigDecimal rmCashBackAmount) {
        this.rmCashBackAmount = rmCashBackAmount;
    }

    @Column(name = "rm_fund_transfer_amount", columnDefinition = "Decimal(19,2) default '0.00'")
    @JsonProperty
    public BigDecimal getRmFundTransferAmount() {
        return rmFundTransferAmount;
    }

    public void setRmFundTransferAmount(BigDecimal rmFundTransferAmount) {
        this.rmFundTransferAmount = rmFundTransferAmount;
    }
}
