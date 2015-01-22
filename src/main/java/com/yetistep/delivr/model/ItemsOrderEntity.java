package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/15/14
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemsOrderEntity")
@Table(name = "items_orders")
public class ItemsOrderEntity implements Serializable {

    private Integer id;
    private ItemEntity item;
    private OrderEntity order;
    private Integer quantity;
    private BigDecimal itemTotal;
    private Boolean availabilityStatus;
    private String note;
    private String customItem;
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

    @ManyToOne
    @JoinColumn(name = "item_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

    @JsonProperty
    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "item_total", precision = 19, scale = 2)
    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    @Column(name="availability_status", columnDefinition = "TINYINT(1) default true")
    public Boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Column(name="note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Column(name = "custom_item")
    public String getCustomItem() {
        return customItem;
    }

    public void setCustomItem(String customItem) {
        this.customItem = customItem;
    }

    @OneToMany(mappedBy = "itemOrder", cascade = CascadeType.ALL, orphanRemoval=true)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<ItemsOrderAttributeEntity> getItemOrderAttributes() {
        return itemOrderAttributes;
    }

    public void setItemOrderAttributes(List<ItemsOrderAttributeEntity> itemOrderAttributes) {
        this.itemOrderAttributes = itemOrderAttributes;
    }
}
