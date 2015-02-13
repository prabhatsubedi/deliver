package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/7/15
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "CartAttributesEntity")
@Table(name = "cart_attributes")
@DynamicUpdate
public class CartAttributesEntity {
    private Integer id;
    private ItemsAttributeEntity itemsAttribute;
    private CartEntity cart;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "items_attribute_id")
    public ItemsAttributeEntity getItemsAttribute() {
        return itemsAttribute;
    }

    @JsonProperty
    public void setItemsAttribute(ItemsAttributeEntity itemsAttribute) {
        this.itemsAttribute = itemsAttribute;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cart_id")
    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
    }
}
