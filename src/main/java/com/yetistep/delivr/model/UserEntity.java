package com.yetistep.delivr.model;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.Gender;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = UserEntity.class)
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
    private String street;
    private String city;
    private String state;
    private String country;
    private String countryCode;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    @JsonManagedReference("user-dboy")
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

    @OneToOne(mappedBy = "user")
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }


    @Column(name = "username", nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name="gender", nullable = true)
    @Type(type="com.yetistep.delivr.enums.GenderCustom")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(name = "street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "city", nullable = false)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "state", nullable = false)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "country", nullable = false)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "country_code", nullable = false)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "mobile_number", nullable = false)
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Column(name = "mobile_verification_status", nullable = false)
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

    @Column(name = "black_list_status", nullable = false)
    public Boolean getBlacklistStatus() {
        return blacklistStatus;
    }

    public void setBlacklistStatus(Boolean blacklistStatus) {
        this.blacklistStatus = blacklistStatus;
    }

    @Column(name = "verification_status", nullable = false)
    public Boolean getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(Boolean verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "verification_code")
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Column(name = "subscribe_newsletter", nullable = false)
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
}
