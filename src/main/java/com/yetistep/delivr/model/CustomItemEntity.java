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
@Entity(name = "CustomItemEntity")
@Table(name = "custom_items")
public class CustomItemEntity {
    private Integer id;
    private String name;
    /*Transient Variable*/
    private String imageUrl;
    private ItemsOrderEntity itemsOrder;

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

    @Transient
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "items_order_id")
    public ItemsOrderEntity getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(ItemsOrderEntity itemsOrder) {
        this.itemsOrder = itemsOrder;
    }
}
