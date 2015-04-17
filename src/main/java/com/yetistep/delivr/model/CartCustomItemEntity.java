package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/26/15
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "CartCustomItemEntity")
@Table(name = "cart_custom_items")
public class CartCustomItemEntity {
    private Integer id;
    private String name;
    private String editedName;
    /*Transient Variable*/
    private String imageUrl;
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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "edited_name")
    public String getEditedName() {
        return editedName;
    }

    public void setEditedName(String editedName) {
        this.editedName = editedName;
    }

    @Transient
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
    }
}
