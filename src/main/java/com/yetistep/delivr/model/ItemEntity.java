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
    private Set<ItemsImageEntity> itemsImage = new HashSet<ItemsImageEntity>();
    private String Description;
    private Integer available_quantity;
    private Timestamp available_start_time;
    private Timestamp available_end_time;
    private Set<ItemsStoreEntity> itemsStores = new HashSet<ItemsStoreEntity>();
    private Set<ItemsAttributeEntity> attributes = new HashSet<ItemsAttributeEntity>();
    private Set<OrderEntity> order = new HashSet<OrderEntity>();
    private Integer max_order_quantity;
    private Integer min_order_quantity;
    private Timestamp created_date;
    private Timestamp modified_date;
    private Integer listing_days;
    private Boolean payment_method_cd;  //cash on demand
    private Boolean payment_method_cc; //credit card
    private Integer unit_price;
    private String currency_type;
    private String multi_select_offer;
    private String single_select_offer;
    private String additional_offer;
    private Integer approx_size;
    private Integer approx_weight;


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

    @Column(name = "description")
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Column(name = "available_quantity")
    public Integer getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(Integer available_quantity) {
        this.available_quantity = available_quantity;
    }

    @Column(name = "available_start_time")
    public Timestamp getAvailable_start_time() {
        return available_start_time;
    }

    public void setAvailable_start_time(Timestamp available_start_time) {
        this.available_start_time = available_start_time;
    }

    @Column(name = "available_end_time")
    public Timestamp getAvailable_end_time() {
        return available_end_time;
    }

    public void setAvailable_end_time(Timestamp available_end_time) {
        this.available_end_time = available_end_time;
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

    @OneToMany(mappedBy = "item")
    public Set<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(Set<OrderEntity> order) {
        this.order = order;
    }

    @Column(name = "max_order_quantity")
    public Integer getMax_order_quantity() {
        return max_order_quantity;
    }

    public void setMax_order_quantity(Integer max_order_quantity) {
        this.max_order_quantity = max_order_quantity;
    }

    @Column(name = "min_order_quantity")
    public Integer getMin_order_quantity() {
        return min_order_quantity;
    }

    public void setMin_order_quantity(Integer min_order_quantity) {
        this.min_order_quantity = min_order_quantity;
    }

    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    @Column(name = "modified_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = true)
    public Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    @Column(name = "listing_days")
    public Integer getListing_days() {
        return listing_days;
    }

    public void setListing_days(Integer listing_days) {
        this.listing_days = listing_days;
    }

    @Column(name = "payment_method_cd")
    public Boolean getPayment_method_cd() {
        return payment_method_cd;
    }

    public void setPayment_method_cd(Boolean payment_method_cd) {
        this.payment_method_cd = payment_method_cd;
    }

    @Column(name = "payment_method_cc")
    public Boolean getPayment_method_cc() {
        return payment_method_cc;
    }

    public void setPayment_method_cc(Boolean payment_method_cc) {
        this.payment_method_cc = payment_method_cc;
    }

    @Column(name = "unit_price")
    public Integer getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Integer unit_price) {
        this.unit_price = unit_price;
    }

    @Column(name = "currency_type")
    public String getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(String currency_type) {
        this.currency_type = currency_type;
    }

    @Column(name = "multi_select_offer", columnDefinition = "longtext")
    public String getMulti_select_offer() {
        return multi_select_offer;
    }

    public void setMulti_select_offer(String multi_select_offer) {
        this.multi_select_offer = multi_select_offer;
    }

    @Column(name = "single_select_offer", columnDefinition = "longtext")
    public String getSingle_select_offer() {
        return single_select_offer;
    }

    public void setSingle_select_offer(String single_select_offer) {
        this.single_select_offer = single_select_offer;
    }

    @Column(name = "additional_offer")
    public String getAdditional_offer() {
        return additional_offer;
    }

    public void setAdditional_offer(String additional_offer) {
        this.additional_offer = additional_offer;
    }

    @Column(name = "approx_size")
    public Integer getApprox_size() {
        return approx_size;
    }

    public void setApprox_size(Integer approx_size) {
        this.approx_size = approx_size;
    }

    @Column(name = "approx_weight")
    public Integer getApprox_weight() {
        return approx_weight;
    }

    public void setApprox_weight(Integer approx_weight) {
        this.approx_weight = approx_weight;
    }


}
