package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
    private String attribute;
    private BigDecimal unitPrice;
    private Boolean selected; //Transient Variable
    private ItemsAttributesTypeEntity type;
    private List<CartAttributesEntity> cartAttributes;
    private List<ItemsOrderAttributeEntity> itemOrderAttributes;

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
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @OneToMany(mappedBy = "itemsAttribute", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<CartAttributesEntity> getCartAttributes() {
        return cartAttributes;
    }

    public void setCartAttributes(List<CartAttributesEntity> cartAttributes) {
        this.cartAttributes = cartAttributes;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "itemsAttribute")
    public List<ItemsOrderAttributeEntity> getItemOrderAttributes() {
        return itemOrderAttributes;
    }

    public void setItemOrderAttributes(List<ItemsOrderAttributeEntity> itemOrderAttributes) {
        this.itemOrderAttributes = itemOrderAttributes;
    }
}
