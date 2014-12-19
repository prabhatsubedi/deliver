package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemsAttributeEntity")
@Table(name = "items_attributes")
public class ItemsAttributeEntity implements Serializable {

    private Integer id;
    private ItemsAttributesTypeEntity type;
    private String attribute;
    private Integer unitPrice;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_attribute_type_id")
    public ItemsAttributesTypeEntity getType() {
        return type;
    }

    public void setType(ItemsAttributesTypeEntity type) {
        this.type = type;
    }

    @Column(name = "attribute")
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Column(name = "unit_price")
    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }
}
