package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.util.JsonDateSerializer;
import com.yetistep.delivr.util.JsonTimeDeserializer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/9/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "StoresBrandEntity")
@Table(name="stores_brands")
public class StoresBrandEntity implements Serializable {

   private Integer id;
   private String brandName;
   private Date openingTime;
   private Date closingTime;
   private String brandLogo;
   private String brandImage;
   private String brandUrl;
   private Boolean featured;
   private Integer priority;
   private Timestamp createdDate;
   private Status status;
   private BigDecimal processingCharge;  // processing charge by the koolkat for the merchant
   private Boolean partnershipStatus;  // partnership status of merchant for the brand
   private BigDecimal discountInDeliveryFee;
   private BigDecimal defaultCommissionPcn;
   private BigDecimal deliveryFee;
   private BigDecimal deliveryFeeLimit;
    //Transient Variable
   private Boolean openStatus;
   private Integer merchantId; //Transient Variable
   private BigDecimal minOrderAmount;
   private Integer countStore; //Transient Variable
   private String nearestStoreLocation; //Transient Variable
   private MerchantEntity merchant;
   private List<StoreEntity> store;
   private List<BrandsCategoryEntity> brandsCategory;
   private List<ItemEntity> items;
   private List<CategoryEntity> categories;
   private List<CartEntity> carts;

   private String vatNo;
   private String panNo;
   private Boolean vatInclusive; // if not vat inclusive then vat exclusive



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "merchant_id")
    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }


    @OneToMany(mappedBy = "storesBrand", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<StoreEntity> getStore() {
        return store;
    }

    public void setStore(List<StoreEntity> store) {
        this.store = store;
    }

    @OneToMany(mappedBy = "storesBrand", cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    public List<BrandsCategoryEntity> getBrandsCategory() {
        return brandsCategory;
    }

    public void setBrandsCategory(List<BrandsCategoryEntity> brandsCategory) {
        this.brandsCategory = brandsCategory;
    }

    @OneToMany(mappedBy = "storesBrand")
    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    @OneToMany(mappedBy = "storesBrand")
    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    @Column(name = "brand_name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Temporal(TemporalType.TIME)
    @JsonDeserialize(using = JsonTimeDeserializer.class)
    @Column(name = "opening_time")
    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    @Temporal(TemporalType.TIME)
    @JsonDeserialize(using = JsonTimeDeserializer.class)
    @Column(name = "closing_time")
    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    @Column(name = "brand_logo")
    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    @Column(name = "brand_image")
    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }

    @Column(name = "brand_url")
    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    @Column(name = "featured", columnDefinition = "TINYINT(1) default '0'")
    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "created_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "processing_charge")
    public BigDecimal getProcessingCharge() {
        return processingCharge;
    }

    public void setProcessingCharge(BigDecimal processingCharge) {
        this.processingCharge = processingCharge;
    }

    @Column(name = "partnership_status")
    public Boolean getPartnershipStatus() {
        return partnershipStatus;
    }

    public void setPartnershipStatus(Boolean partnershipStatus) {
        this.partnershipStatus = partnershipStatus;
    }

    @Column(name = "discount_in_delivery_fee", columnDefinition = "Decimal(10,2) default '0.00'")
    public BigDecimal getDiscountInDeliveryFee() {
        return discountInDeliveryFee;
    }

    public void setDiscountInDeliveryFee(BigDecimal discountInDeliveryFee) {
        this.discountInDeliveryFee = discountInDeliveryFee;
    }

    @Column(name = "default_commission")
    public BigDecimal getDefaultCommissionPcn() {
        return defaultCommissionPcn;
    }

    public void setDefaultCommissionPcn(BigDecimal defaultCommissionPcn) {
        this.defaultCommissionPcn = defaultCommissionPcn;
    }

    @Column(name = "delivery_fee")
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    @Column(name = "delivery_fee_limit")
    public BigDecimal getDeliveryFeeLimit() {
        return deliveryFeeLimit;
    }

    public void setDeliveryFeeLimit(BigDecimal deliveryFeeLimit) {
        this.deliveryFeeLimit = deliveryFeeLimit;
    }

    @Transient
    public Boolean getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Boolean openStatus) {
        this.openStatus = openStatus;
    }

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "storesBrand")
    public List<CartEntity> getCarts() {
        return carts;
    }

    public void setCarts(List<CartEntity> carts) {
        this.carts = carts;
    }

    @Column(name = "min_order_amount", nullable = false)
    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    @Column(name = "vat_no")
    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    @Column(name = "pan_no")
    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    @Column(name = "vat_inclusive")
    public Boolean getVatInclusive() {
        return vatInclusive;
    }

    public void setVatInclusive(Boolean vatInclusive) {
        this.vatInclusive = vatInclusive;
    }

    @Transient
    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    @Transient
    public Integer getCountStore() {
        return countStore;
    }

    public void setCountStore(Integer countStore) {
        this.countStore = countStore;
    }

    @Transient
    public String getNearestStoreLocation() {
        return nearestStoreLocation;
    }

    public void setNearestStoreLocation(String nearestStoreLocation) {
        this.nearestStoreLocation = nearestStoreLocation;
    }
}
