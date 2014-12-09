package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.VehicleType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

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
    private Integer orderVerificationCode;
    private Boolean orderVerificationStatus;
    private Integer deliveryStatus;
    private Integer orderStatus;
    private ItemEntity item;
    private DeliveryBoyEntity deliveryBoy;
    private CategoryEntity customer;
    private StoreEntity store;
    private BigDecimal customerChargeableDistance; //Paid by customer
    private BigDecimal systemChargeableDistance; //Paid by system
    private BigDecimal itemTotal;
    private BigDecimal totalCost;
    private BigDecimal transportationCharge;
    private BigDecimal systemServiceCharge;
    private BigDecimal deliveryCharge;
    private BigDecimal grandTotal;
    private BigDecimal deliveryBoyShare;
    private BigDecimal systemShare;
    private Timestamp orderDate;


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
    public Integer getOrderVerificationCode() {
        return orderVerificationCode;
    }

    public void setOrderVerificationCode(Integer orderVerificationCode) {
        this.orderVerificationCode = orderVerificationCode;
    }

    @Column(name = "order_verification_status")
    public Boolean getOrderVerificationStatus() {
        return orderVerificationStatus;
    }

    public void setOrderVerificationStatus(Boolean orderVerificationStatus) {
        this.orderVerificationStatus = orderVerificationStatus;
    }

    @Column(name = "delivery_status")
    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Column(name = "order_status")
    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
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
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy (DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
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
    public BigDecimal getCustomerChargeableDistance() {
        return customerChargeableDistance;
    }

    public void setCustomerChargeableDistance(BigDecimal customerChargeableDistance) {
        this.customerChargeableDistance = customerChargeableDistance;
    }

    @Column(name = "system_chargeable_distance", precision = 4, scale = 2)
    public BigDecimal getSystemChargeableDistance() {
        return systemChargeableDistance;
    }

    public void setSystemChargeableDistance(BigDecimal systemChargeableDistance) {
        this.systemChargeableDistance = systemChargeableDistance;
    }

    @Column(name = "item_total", precision = 4, scale = 2)
    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    @Column(name = "total_cost", precision = 4, scale = 2)
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Column(name = "transportaion_charge", precision = 4, scale = 2)
    public BigDecimal getTransportationCharge() {
        return transportationCharge;
    }

    public void setTransportationCharge(BigDecimal transportationCharge) {
        this.transportationCharge = transportationCharge;
    }

    @Column(name = "system_service_charge", precision = 4, scale = 2)
    public BigDecimal getSystemServiceCharge() {
        return systemServiceCharge;
    }

    public void setSystemServiceCharge(BigDecimal systemServiceCharge) {
        this.systemServiceCharge = systemServiceCharge;
    }

    @Column(name = "delivery_charge", precision = 4, scale = 2)
    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    @Column(name = "grand_total", precision = 4, scale = 2)
    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Column(name = "delivery_boy_share", precision = 4, scale = 2)
    public BigDecimal getDeliveryBoyShare() {
        return deliveryBoyShare;
    }

    public void setDeliveryBoyShare (BigDecimal deliveryBoyShare) {
        this.deliveryBoyShare = deliveryBoyShare;
    }

    @Column(name = "system_share", precision = 4, scale = 2)
    public BigDecimal getSystemShare() {
        return systemShare ;
    }

    public void setSystemShare(BigDecimal systemShare) {
        this.systemShare = systemShare;
    }


    @Column(name = "order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
}
