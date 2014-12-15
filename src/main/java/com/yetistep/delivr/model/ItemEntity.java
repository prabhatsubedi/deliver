package com.yetistep.delivr.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
    private CategoryEntity category;
    private String name;
    private Set<ItemsImageEntity> itemsImage;
    private Set<ItemsOrderEntity> itemsOrder;
    private String Description;
    private Integer availableQuantity;
    private Timestamp availableStartTime;
    private Timestamp availableEndTime;
    private Set<ItemsStoreEntity> itemsStores;
    private Set<ItemsAttributeEntity> attributes;
    //private Set<OrderEntity> order;
    private Integer maxOrderQuantity;
    private Integer minOrderQuantity;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private Integer listingDays;
    private Boolean paymentMethodCd;  //cash on demand
    private Boolean paymentMethodCc; //credit card
    private Integer unitPrice;
    private String currencyType;
    private String multiSelectOffer;
    private String singleSelectOffer;
    private String additionalOffer;
    private Integer approxSize;
    private Integer approxWeight;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "item")
    public Set<ItemsImageEntity> getItemsImage() {
        return itemsImage;
    }

    public void setItemsImage(Set<ItemsImageEntity> itemsImage) {
        this.itemsImage = itemsImage;
    }

    @OneToMany(mappedBy = "item")
    public Set<ItemsOrderEntity> getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(Set<ItemsOrderEntity> itemsOrder) {
        this.itemsOrder = itemsOrder;
    }

    @Column(name = "description")
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Column(name = "available_quantity")
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Column(name = "available_start_time")
    public Timestamp getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(Timestamp availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    @Column(name = "available_end_time")
    public Timestamp getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(Timestamp availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    @OneToMany(mappedBy = "item")
    public Set<ItemsStoreEntity> getItemsStores() {
        return itemsStores;
    }

    public void setItemsStores(Set<ItemsStoreEntity> itemsStores) {
        this.itemsStores = itemsStores;
    }

    @OneToMany(mappedBy = "item")
    public Set<ItemsAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ItemsAttributeEntity> attributes) {
        this.attributes = attributes;
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

    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "modified_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = true)
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Column(name = "listing_days")
    public Integer getListingDays() {
        return listingDays;
    }

    public void setListingDays(Integer listing_days) {
        this.listingDays = listingDays;
    }

    @Column(name = "payment_method_cd", columnDefinition = "TINYINT(1)")
    public Boolean getPaymentMethodCd() {
        return paymentMethodCd;
    }

    public void setPaymentMethodCd(Boolean paymentMethodCd) {
        this.paymentMethodCd = paymentMethodCd;
    }

    @Column(name = "payment_method_cc", columnDefinition = "TINYINT(1)")
    public Boolean getPaymentMethodCc() {
        return paymentMethodCc;
    }

    public void setPaymentMethodCc(Boolean paymentMethodCc) {
        this.paymentMethodCc = paymentMethodCc;
    }

    @Column(name = "unit_price")
    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Column(name = "currency_type")
    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    @Column(name = "multi_select_offer", columnDefinition = "longtext")
    public String getMultiSelectOffer() {
        return multiSelectOffer;
    }

    public void setMultiSelectOffer(String multiSelectOffer) {
        this.multiSelectOffer = multiSelectOffer;
    }

    @Column(name = "single_select_offer", columnDefinition = "longtext")
    public String getSingleSelectOffer() {
        return singleSelectOffer;
    }

    public void setSingleSelectOffer(String singleSelectOffer) {
        this.singleSelectOffer = singleSelectOffer;
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


}
