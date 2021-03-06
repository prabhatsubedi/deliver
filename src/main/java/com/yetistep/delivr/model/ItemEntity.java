package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.util.JsonDateSerializer;
import com.yetistep.delivr.util.JsonTimeDeserializer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemEntity")
@Table(name = "items")
public class ItemEntity implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Integer availableQuantity;
    private Date availableStartTime;
    private Date availableEndTime;
    private Integer maxOrderQuantity;
    private Integer minOrderQuantity;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private BigDecimal unitPrice;
    private String currencyType;
    private String additionalOffer;
    private Integer approxSize;
    private Integer approxWeight;
    private String returnPolicy;
    private Integer deliveryFee;
    private String promoCode;
    private BigDecimal vat; // vat charge by merchant
    private BigDecimal serviceCharge; //service charge by merchant
    private BigDecimal commissionPercentage; //commission percentage from merchant to koolkat
    private Status status;
    private String tags;
    private String imageUrl; //Transient Value
    private String brandName; //Transient Value
    private Integer orderQuantity; //Transient Value
    private String currency; //Transient Value
    private Integer brandId; //Transient Value
    private Date openingTime; //Transient Value
    private Date closingTime; //Transient Value
    private Boolean isCustomItem = Boolean.FALSE; //Transient Value
    private Boolean defaultImage; //Transient Value
    private String editedName;
    private BigDecimal cashBackAmount;
    private BigDecimal mrp;

    private CategoryEntity category;
    private StoresBrandEntity storesBrand;
    private List<ItemsImageEntity> itemsImage;
    private List<ItemsOrderEntity> itemsOrder;
    private List<ItemsStoreEntity> itemsStores;
    private List<ItemsAttributesTypeEntity> attributesTypes;
    private List<CartEntity> carts;


    private BigDecimal deliveryFeeLimit;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }


    @ManyToOne
    @JoinColumn(name = "brand_id")
    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    public List<ItemsImageEntity> getItemsImage() {
        return itemsImage;
    }

    public void setItemsImage(List<ItemsImageEntity> itemsImage) {
        this.itemsImage = itemsImage;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "item")
    public List<ItemsOrderEntity> getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(List<ItemsOrderEntity> itemsOrder) {
        this.itemsOrder = itemsOrder;
    }

    @Type(type="text")
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "available_quantity")
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Temporal(TemporalType.TIME)
    @JsonDeserialize(using = JsonTimeDeserializer.class)
    @Column(name = "available_start_time")
    public Date getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(Date availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    @Temporal(TemporalType.TIME)
    @JsonDeserialize(using = JsonTimeDeserializer.class)
    @Column(name = "available_end_time")
    public Date getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(Date availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    @Transient
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<ItemsStoreEntity> getItemsStores() {
        return itemsStores;
    }

    public void setItemsStores(List<ItemsStoreEntity> itemsStores) {
        this.itemsStores = itemsStores;
    }

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<ItemsAttributesTypeEntity> getAttributesTypes() {
        return attributesTypes;
    }

    public void setAttributesTypes(List<ItemsAttributesTypeEntity> attributesTypes) {
        this.attributesTypes = attributesTypes;
    }

    /*@OneToMany(mappedBy = "item")
    public Set<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(Set<OrderEntity> order) {
        this.order = order;
    }*/

    @Column(name = "max_order_quantity")
    public Integer getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(Integer maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    @Column(name = "min_order_quantity")
    public Integer getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "created_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "modified_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Transient
    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    @Transient
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "unit_price")
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Column(name = "currency_type")
    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    @Column(name = "additional_offer")
    public String getAdditionalOffer() {
        return additionalOffer;
    }

    public void setAdditionalOffer(String additionalOffer) {
        this.additionalOffer = additionalOffer;
    }

    @Column(name = "approx_size")
    public Integer getApproxSize() {
        return approxSize;
    }

    public void setApproxSize(Integer approxSize) {
        this.approxSize = approxSize;
    }

    @Column(name = "approx_weight")
    public Integer getApproxWeight() {
        return approxWeight;
    }

    public void setApproxWeight(Integer approxWeight) {
        this.approxWeight = approxWeight;
    }

    @Column(name = "return_policy")
    public String getReturnPolicy() {
        return returnPolicy;
    }

    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    @Column(name = "delivery_fee")
    public Integer getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Integer deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    @Column(name = "promo_code")
    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @Column(name = "vat")
    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    @Column(name = "service_charge")
    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    @Column(name = "commission_percentage")
    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "tags")
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
    public List<CartEntity> getCarts() {
        return carts;
    }

    public void setCarts(List<CartEntity> carts) {
        this.carts = carts;
    }

    @Column(name = "cash_back_amount", columnDefinition = "Decimal(19,2) default '0.00'")
    public BigDecimal getCashBackAmount() {
        return cashBackAmount;
    }

    public void setCashBackAmount(BigDecimal cashBackAmount) {
        this.cashBackAmount = cashBackAmount;
    }

    @Column(name = "mrp", columnDefinition = "Decimal(19,2) default '0.00'")
    public BigDecimal getMrp() {
        return mrp;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }

    @Transient
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Transient
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Transient
    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    @Transient
    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    @Transient
    public Boolean getIsCustomItem() {
        return isCustomItem;
    }

    public void setIsCustomItem(Boolean customItem) {
        isCustomItem = customItem;
    }

    @Transient
    public Boolean getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(Boolean defaultImage) {
        this.defaultImage = defaultImage;
    }

    @Transient
    public String getEditedName() {
        return editedName;
    }

    public void setEditedName(String editedName) {
        this.editedName = editedName;
    }
    @Transient
    public BigDecimal getDeliveryFeeLimit() {
        return deliveryFeeLimit;
    }

    public void setDeliveryFeeLimit(BigDecimal deliveryFeeLimit) {
        this.deliveryFeeLimit = deliveryFeeLimit;
    }
}
