package com.yetistep.delivr.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

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
    private BigDecimal item_total;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    public OrderEntity getOrder() {
        return order;
    }

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

    @Column(name = "item_total", precision = 4, scale = 2)
    public BigDecimal getItem_total() {
        return item_total;
    }

    public void setItem_total(BigDecimal item_total) {
        this.item_total = item_total;
    }
}
