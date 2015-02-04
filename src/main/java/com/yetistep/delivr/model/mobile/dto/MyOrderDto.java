package com.yetistep.delivr.model.mobile.dto;

import com.yetistep.delivr.enums.JobOrderStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/4/15
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyOrderDto {
    private Integer orderId;
    private String brandLogo;
    private String brandName;
    private JobOrderStatus orderStatus;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
