package com.yetistep.delivr.model.mobile.dto;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/6/15
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ItemDto {
    private String currency;
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal serviceCharge;
    private BigDecimal vat;
    private String imageUrl;
    private Integer brandId;
    private Boolean defaultImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(Boolean defaultImage) {
        this.defaultImage = defaultImage;
    }
}
