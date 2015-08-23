package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/7/15
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "CartEntity")
@Table(name = "cart")
@DynamicUpdate
public class CartEntity {
    private Integer id;
    private Integer orderQuantity;
    private String note;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private ItemEntity item;
    private StoresBrandEntity storesBrand;
    private CustomerEntity customer;
    private List<CartAttributesEntity> cartAttributes;
    private CartCustomItemEntity cartCustomItem;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "order_quantity")
    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    @Type(type="text")
    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @ManyToOne
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
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

    @ManyToOne
    @JoinColumn(name = "brand_id")
    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    @ManyToOne
    @JoinColumn(name = "customer_fb_id", referencedColumnName = "facebook_id")
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    public List<CartAttributesEntity> getCartAttributes() {
        return cartAttributes;
    }

    public void setCartAttributes(List<CartAttributesEntity> cartAttributes) {
        this.cartAttributes = cartAttributes;
    }

    @OneToOne(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true, fetch = FetchType.LAZY)
    public CartCustomItemEntity getCartCustomItem() {
        return cartCustomItem;
    }

    public void setCartCustomItem(CartCustomItemEntity cartCustomItem) {
        this.cartCustomItem = cartCustomItem;
    }
}
