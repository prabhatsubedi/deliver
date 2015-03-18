package com.yetistep.delivr.model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/16/15
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ValidateMobileEntity")
@Table(name = "validate_mobile")
public class ValidateMobileEntity {
    private Integer id;
    private String mobileNo;
    private String verificationCode;
    private Boolean verifiedByUser;
    private Integer totalSmsSend;
    private UserEntity user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "mobile_no")
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Column(name = "verification_code")
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Column(name = "verified_by_user")
    public Boolean getVerifiedByUser() {
        return verifiedByUser;
    }

    public void setVerifiedByUser(Boolean verifiedByUser) {
        this.verifiedByUser = verifiedByUser;
    }

    @Column(name = "total_sms_send")
    public Integer getTotalSmsSend() {
        return totalSmsSend;
    }

    public void setTotalSmsSend(Integer totalSmsSend) {
        this.totalSmsSend = totalSmsSend;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
