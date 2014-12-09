package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.VehicleType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="OrderEntity")
@Table(name = "orders")
public class OrderEntity implements Serializable {


    private Integer id;
    private Integer order_verification_code;
    private Boolean order_verification_status;
    private Integer delivery_status;
    private Integer order_status;
    private ItemEntity item;
    private DeliveryBoyEntity delivery_boy;
    private CategoryEntity customer;
    private StoreEntity store;
    private BigDecimal customer_chargeable_distance; //Paid by customer
    private BigDecimal system_chargeable_distance; //Paid by system
    private BigDecimal item_total;
    private BigDecimal total_cost;
    private BigDecimal transportaion_charge;
    private BigDecimal system_service_charge;
    private BigDecimal delivery_charge;
    private BigDecimal grand_total;
    private BigDecimal delivery_boy_share;
    private BigDecimal system_share;
    private Timestamp order_date;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "order_verification_code")
    public Integer getOrder_verification_code() {
        return order_verification_code;
    }

    public void setOrder_verification_code(Integer order_verification_code) {
        this.order_verification_code = order_verification_code;
    }

    @Column(name = "order_verification_status")
    public Boolean getOrder_verification_status() {
        return order_verification_status;
    }

    public void setOrder_verification_status(Boolean order_verification_status) {
        this.order_verification_status = order_verification_status;
    }

    @Column(name = "delivery_status")
    public Integer getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(Integer delivery_status) {
        this.delivery_status = delivery_status;
    }

    @Column(name = "order_status")
    public Integer getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_boy_id")
    public DeliveryBoyEntity getDelivery_boy() {
        return delivery_boy;
    }

    public void setDelivery_boy(DeliveryBoyEntity delivery_boy) {
        this.delivery_boy = delivery_boy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    public CategoryEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CategoryEntity customer) {
        this.customer = customer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    @Column(name = "customer_chargeable_distance", precision = 4, scale = 2)
    public BigDecimal getCustomer_chargeable_distance() {
        return customer_chargeable_distance;
    }

    public void setCustomer_chargeable_distance(BigDecimal customer_chargeable_distance) {
        this.customer_chargeable_distance = customer_chargeable_distance;
    }

    @Column(name = "system_chargeable_distance", precision = 4, scale = 2)
    public BigDecimal getSystem_chargeable_distance() {
        return system_chargeable_distance;
    }

    public void setSystem_chargeable_distance(BigDecimal system_chargeable_distance) {
        this.system_chargeable_distance = system_chargeable_distance;
    }

    @Column(name = "item_total", precision = 4, scale = 2)
    public BigDecimal getItem_total() {
        return item_total;
    }

    public void setItem_total(BigDecimal item_total) {
        this.item_total = item_total;
    }

    @Column(name = "total_cost", precision = 4, scale = 2)
    public BigDecimal getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(BigDecimal total_cost) {
        this.total_cost = total_cost;
    }

    @Column(name = "transportaion_charge", precision = 4, scale = 2)
    public BigDecimal getTransportaion_charge() {
        return transportaion_charge;
    }

    public void setTransportaion_charge(BigDecimal transportaion_charge) {
        this.transportaion_charge = transportaion_charge;
    }

    @Column(name = "system_service_charge", precision = 4, scale = 2)
    public BigDecimal getSystem_service_charge() {
        return system_service_charge;
    }

    public void setSystem_service_charge(BigDecimal system_service_charge) {
        this.system_service_charge = system_service_charge;
    }

    @Column(name = "delivery_charge", precision = 4, scale = 2)
    public BigDecimal getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(BigDecimal delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    @Column(name = "grand_total", precision = 4, scale = 2)
    public BigDecimal getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(BigDecimal grand_total) {
        this.grand_total = grand_total;
    }

    @Column(name = "delivery_boy_share", precision = 4, scale = 2)
    public BigDecimal getDelivery_boy_share() {
        return delivery_boy_share;
    }

    public void setDelivery_boy_share(BigDecimal delivery_boy_share) {
        this.delivery_boy_share = delivery_boy_share;
    }

    @Column(name = "system_share", precision = 4, scale = 2)
    public BigDecimal getSystem_share() {
        return system_share;
    }

    public void setSystem_share(BigDecimal system_share) {
        this.system_share = system_share;
    }

    @Column(name = "order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Timestamp order_date) {
        this.order_date = order_date;
    }
}
