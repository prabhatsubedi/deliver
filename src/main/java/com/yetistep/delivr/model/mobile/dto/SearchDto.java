package com.yetistep.delivr.model.mobile.dto;

import com.yetistep.delivr.model.mobile.PageInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 2/12/15
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchDto {

    // ============ Currency ========================= //
    private String currency;

    //Size Concern
    private Integer totalItemSize;
    private Integer totalStoreSize;

    //============ Section for Item Search =================//
    private Integer itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private String itemImageUrl;
    private String storeStreet;
    private String additionalOffer;
    private Boolean isItem;

    //============ Section for Store Search =================//
    private Integer brandId;
    private String brandName;
    private Date openingTime;
    private Date closingTime;
    private String brandLogo;
    private String brandImage;
    private String brandUrl;
    private Boolean openStatus;

    // ========= Search Object ==========================//
    private PageInfo pageInfo;
    private List<SearchDto> searchList;



    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getStoreStreet() {
        return storeStreet;
    }

    public void setStoreStreet(String storeStreet) {
        this.storeStreet = storeStreet;
    }

    public String getAdditionalOffer() {
        return additionalOffer;
    }

    public void setAdditionalOffer(String additionalOffer) {
        this.additionalOffer = additionalOffer;
    }

    public Boolean getItem() {
        return isItem;
    }

    public void setItem(Boolean item) {
        isItem = item;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    public Boolean getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Boolean openStatus) {
        this.openStatus = openStatus;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<SearchDto> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchDto> searchList) {
        this.searchList = searchList;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getTotalItemSize() {
        return totalItemSize;
    }

    public void setTotalItemSize(Integer totalItemSize) {
        this.totalItemSize = totalItemSize;
    }

    public Integer getTotalStoreSize() {
        return totalStoreSize;
    }

    public void setTotalStoreSize(Integer totalStoreSize) {
        this.totalStoreSize = totalStoreSize;
    }
}
