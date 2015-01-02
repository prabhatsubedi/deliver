package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;
import com.yetistep.delivr.util.JsonTimeDeserializer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Date;

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
    private StoresBrandEntity storesBrand;
    private String name;
    private List<ItemsImageEntity> itemsImage;
    private List<ItemsOrderEntity> itemsOrder;
    private String description;
    private Integer availableQuantity;
    private Date availableStartTime;
    private Date availableEndTime;
    private List<ItemsStoreEntity> itemsStores;
    private List<ItemsAttributesTypeEntity> attributesTypes;
    //private Set<OrderEntity> order;
    private Integer maxOrderQuantity;
    private Integer minOrderQuantity;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    //private Timestamp validTill;
    private Boolean paymentMethodCd;  //cash on demand
    private Boolean paymentMethodCc; //credit card
    private Integer unitPrice;
    private String currencyType;
    //private Boolean multiSelectOffer;
    //private Boolean singleSelectOffer;
    private String additionalOffer;
    private Integer approxSize;
    private Integer approxWeight;
    private String returnPolicy;
    private Integer deliveryFee;
    private String promoCode;
    private BigDecimal vat;
    private BigDecimal serviceCharge;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<ItemsImageEntity> getItemsImage() {
        return itemsImage;
    }

    public void setItemsImage(List<ItemsImageEntity> itemsImage) {
        this.itemsImage = itemsImage;
    }

    @OneToMany(mappedBy = "item")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<ItemsOrderEntity> getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(List<ItemsOrderEntity> itemsOrder) {
        this.itemsOrder = itemsOrder;
    }

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

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<ItemsStoreEntity> getItemsStores() {
        return itemsStores;
    }

    public void setItemsStores(List<ItemsStoreEntity> itemsStores) {
        this.itemsStores = itemsStores;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
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
    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "modified_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = true)
    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /*@Column(name = "valid_till")
    public Timestamp getValidTill() {
        return validTill;
    }

    public void setValidTill(Timestamp validTill) {
        this.validTill = validTill;
    }*/

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

}
