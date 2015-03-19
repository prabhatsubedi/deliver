package com.yetistep.delivr.model.mobile.dto;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/18/15
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class SMSDto {
    private Integer id;
    private String mobileNo;
    private Integer totalSmsSend;
    private String fullName;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getTotalSmsSend() {
        return totalSmsSend;
    }

    public void setTotalSmsSend(Integer totalSmsSend) {
        this.totalSmsSend = totalSmsSend;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
