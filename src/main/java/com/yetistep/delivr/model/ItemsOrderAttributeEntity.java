package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/21/15
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemsOrderAttributeEntity")
@Table(name = "items_order_attribute")
public class ItemsOrderAttributeEntity {
    private Integer id;
    private ItemsAttributeEntity itemsAttribute;
    private ItemsOrderEntity itemOrder;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="items_attribute_id")
    public ItemsAttributeEntity getItemsAttribute() {
        return itemsAttribute;
    }

    @JsonProperty
    public void setItemsAttribute(ItemsAttributeEntity itemsAttribute) {
        this.itemsAttribute = itemsAttribute;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_order_id")
    public ItemsOrderEntity getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(ItemsOrderEntity itemOrder) {
        this.itemOrder = itemOrder;
    }
}
