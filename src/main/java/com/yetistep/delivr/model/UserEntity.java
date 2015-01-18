package com.yetistep.delivr.model;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.Gender;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.util.JsonDateSerializer;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/4/14
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name="UserEntity")
@Table(name = "users")
@DynamicUpdate
public class UserEntity implements Serializable {

    private Integer id;
    private RoleEntity role;
    private String username;
    private String password;
    private DeliveryBoyEntity deliveryBoy;
    private MerchantEntity merchant;
    private CustomerEntity customer;
    private String fullName;
    private Gender gender;
    private String mobileNumber;
    private Boolean mobileVerificationStatus;
    private String emailAddress;
    private String profileImage;
    private Timestamp lastActivityDate;
    private Timestamp createdDate;
    private Boolean blacklistStatus;
    private Boolean verifiedStatus;
    private String token;
    private String verificationCode;
    private Boolean subscribeNewsletter;
    private List<ActionLogEntity> actionLogEntities;
    private List<AddressEntity> addresses;
    private UserDeviceEntity userDevice;
    private Status status;
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

    @ManyToOne
    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    @JsonBackReference("dboy-user")
    @OneToOne(mappedBy = "user")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }



    @JsonBackReference("merchant-user")
    @OneToOne(mappedBy = "user")
    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name="gender")
    @Type(type="com.yetistep.delivr.enums.GenderCustom")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(name = "mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Column(name = "mobile_verification_status", columnDefinition = "TINYINT(1)")
    public Boolean getMobileVerificationStatus() {
        return mobileVerificationStatus;
    }

    public void setMobileVerificationStatus(Boolean mobileVerificationStatus) {
        this.mobileVerificationStatus = mobileVerificationStatus;
    }

    @Column(name = "email", unique = true)
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "profile_image")
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "last_activity_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Timestamp lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "black_list_status", columnDefinition = "TINYINT(1)")
    public Boolean getBlacklistStatus() {
        return blacklistStatus;
    }

    public void setBlacklistStatus(Boolean blacklistStatus) {
        this.blacklistStatus = blacklistStatus;
    }

    @Column(name = "verification_status", columnDefinition = "TINYINT(1)")
    public Boolean getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(Boolean verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }

    @JsonIgnore
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonIgnore
    @Column(name = "verification_code")
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Column(name = "subscribe_newsletter", columnDefinition = "TINYINT(1)")
    public Boolean getSubscribeNewsletter() {
        return subscribeNewsletter;
    }

    public void setSubscribeNewsletter(Boolean subscribeNewsletter) {
        this.subscribeNewsletter = subscribeNewsletter;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity")
    public List<ActionLogEntity> getActionLogEntities() {
        return actionLogEntities;
    }

    public void setActionLogEntities(List<ActionLogEntity> actionLogEntities) {
        this.actionLogEntities = actionLogEntities;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    @JsonManagedReference
    @OneToOne(mappedBy = "user" ,fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public UserDeviceEntity getUserDevice() {
        return userDevice;
    }

    public void setUserDevice(UserDeviceEntity userDevice) {
        this.userDevice = userDevice;
    }

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    public List<OrderCancelEntity> getOrderCancelEntities() {
        return orderCancelEntities;
    }

    public void setOrderCancelEntities(List<OrderCancelEntity> orderCancelEntities) {
        this.orderCancelEntities = orderCancelEntities;
    }
}
